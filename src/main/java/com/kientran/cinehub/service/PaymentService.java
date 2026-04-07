package com.kientran.cinehub.service;

import com.kientran.cinehub.config.VNPayConfig;
import com.kientran.cinehub.dto.request.PaymentRequest;
import com.kientran.cinehub.dto.response.PaymentResponse;
import com.kientran.cinehub.entity.Payment;
import com.kientran.cinehub.entity.PremiumPackage;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.repository.PaymentRepository;
import com.kientran.cinehub.repository.PremiumPackageRepository;
import com.kientran.cinehub.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentService {

    final PaymentRepository paymentRepository;
    final PremiumPackageRepository packageRepository;
    final UserRepository userRepository;
    final PremiumSubscriptionService subscriptionService;


    @Value("${vnpay.tmn-code}")
    String vnp_TmnCode;

    @Value("${vnpay.hash-secret}")
    String vnp_HashSecret;

    @Value("${vnpay.api-url}")
    String vnp_PayUrl;

    @Value("${vnpay.return-url}")
    String vnp_ReturnUrl;

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .username(payment.getUser() != null ? payment.getUser().getUsername() : null)
                .packageName(payment.getPremiumPackage() != null ? payment.getPremiumPackage().getPackageName() : null)
                .build();
    }

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request, String username, HttpServletRequest servletRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PremiumPackage premiumPackage = packageRepository.findById(request.getPremiumPackageId())
                .orElseThrow(() -> new RuntimeException("Premium package not found"));

        // 1. Lưu thông tin thanh toán tạm thời vào DB
        Payment payment = Payment.builder()
                .user(user)
                .premiumPackage(premiumPackage)
                .amount(request.getAmount())
                .paymentMethod("VNPAY")
                .status("PENDING")
                .paymentDate(LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);

        // 2. Xây dựng tham số cho VNPay
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = String.valueOf(payment.getId());
        String vnp_IpAddr = VNPayConfig.getIpAddress(servletRequest);
        String vnp_OrderInfo = "Thanh toan goi Premium: " + premiumPackage.getPackageName();
        String vnp_OrderType = "other";

        // VNPay yêu cầu số tiền nhân 100
        long amount = request.getAmount().longValue() * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_Locale", "vn");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // 3. Sắp xếp tham số và tạo Hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnp_PayUrl + "?" + queryUrl;

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .paymentUrl(paymentUrl)
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }

    @Transactional
    public PaymentResponse processPaymentCallback(HttpServletRequest request) {
        // 1. Thu thập tất cả tham số từ VNPay trả về
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        // 2. Lấy chữ ký SecureHash và loại bỏ nó khỏi danh sách băm
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        // 3. Sắp xếp tham số và tạo chuỗi hash data
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            hashData.append(fieldName);
            hashData.append('=');
            // Lưu ý: VNPay trả về đã được URLEncode, nhưng tùy phiên bản
            // bạn có thể cần dùng StandardCharsets.US_ASCII nếu hash không khớp
            hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
            if (itr.hasNext()) {
                hashData.append('&');
            }
        }

        // 4. Kiểm tra chữ ký
        String signValue = VNPayConfig.hmacSHA512(vnp_HashSecret, hashData.toString());
        if (!signValue.equalsIgnoreCase(vnp_SecureHash)) {
            throw new RuntimeException("Chữ ký không hợp lệ! Giao dịch có dấu hiệu bị can thiệp.");
        }

        // 5. Xử lý logic nghiệp vụ khi chữ ký đã khớp
        String txnRef = request.getParameter("vnp_TxnRef");
        String responseCode = request.getParameter("vnp_ResponseCode");

        Payment payment = paymentRepository.findById(Long.parseLong(txnRef))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));

        if ("00".equals(responseCode)) {
            if (!"SUCCESS".equals(payment.getStatus())) {
                payment.setStatus("SUCCESS");
                paymentRepository.save(payment);
                // Kích hoạt gói Premium để đánh dấu User này không phải xem quảng cáo
                subscriptionService.activatePremium(payment);
            }
        } else {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
        }

        return PaymentResponse.builder()
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .build();
    }
}