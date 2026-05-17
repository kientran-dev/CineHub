package com.kientran.cinehub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Gửi email xác nhận thanh toán thành công
     */
    @Async
    public void sendPaymentConfirmation(String toEmail, String fullName,
                                        String packageName, BigDecimal amount,
                                        LocalDateTime paymentDate, Long paymentId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🎬 CineHub - Xác nhận thanh toán thành công #" + paymentId);

            String formattedAmount = NumberFormat.getInstance(new Locale("vi", "VN"))
                    .format(amount) + " VND";
            String formattedDate = paymentDate.format(
                    DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head><meta charset="utf-8"></head>
                    <body style="font-family: Arial, sans-serif; background:#f4f4f4; padding:20px;">
                      <div style="max-width:600px; margin:0 auto; background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);">
                        <div style="background:#dc2626; padding:30px; text-align:center;">
                          <h1 style="color:#fff; margin:0; font-size:28px;">🎬 CineHub</h1>
                          <p style="color:#fca5a5; margin:8px 0 0;">Nền tảng xem phim trực tuyến</p>
                        </div>
                        <div style="padding:30px;">
                          <h2 style="color:#111; margin-top:0;">✅ Thanh toán thành công!</h2>
                          <p style="color:#444;">Xin chào <strong>%s</strong>,</p>
                          <p style="color:#444;">Giao dịch của bạn đã được xử lý thành công. Dưới đây là chi tiết:</p>
                          <table style="width:100%%; border-collapse:collapse; margin:20px 0;">
                            <tr style="background:#f9fafb;">
                              <td style="padding:12px 16px; border:1px solid #e5e7eb; font-weight:bold; width:40%%;">Mã giao dịch</td>
                              <td style="padding:12px 16px; border:1px solid #e5e7eb;">#%d</td>
                            </tr>
                            <tr>
                              <td style="padding:12px 16px; border:1px solid #e5e7eb; font-weight:bold;">Gói Premium</td>
                              <td style="padding:12px 16px; border:1px solid #e5e7eb; color:#dc2626; font-weight:bold;">%s</td>
                            </tr>
                            <tr style="background:#f9fafb;">
                              <td style="padding:12px 16px; border:1px solid #e5e7eb; font-weight:bold;">Số tiền</td>
                              <td style="padding:12px 16px; border:1px solid #e5e7eb; font-weight:bold; font-size:18px;">%s</td>
                            </tr>
                            <tr>
                              <td style="padding:12px 16px; border:1px solid #e5e7eb; font-weight:bold;">Thời gian</td>
                              <td style="padding:12px 16px; border:1px solid #e5e7eb;">%s</td>
                            </tr>
                            <tr style="background:#f9fafb;">
                              <td style="padding:12px 16px; border:1px solid #e5e7eb; font-weight:bold;">Phương thức</td>
                              <td style="padding:12px 16px; border:1px solid #e5e7eb;">VNPay</td>
                            </tr>
                          </table>
                          <div style="background:#fef2f2; border:1px solid #fecaca; border-radius:8px; padding:16px; margin:20px 0;">
                            <p style="margin:0; color:#dc2626; font-weight:bold;">🎉 Tài khoản Premium của bạn đã được kích hoạt!</p>
                            <p style="margin:8px 0 0; color:#444; font-size:14px;">Hãy tận hưởng toàn bộ tính năng Premium trên CineHub!</p>
                          </div>
                          <div style="text-align:center; margin-top:24px;">
                            <a href="http://localhost:5173" style="background:#dc2626; color:#fff; padding:12px 32px; border-radius:8px; text-decoration:none; font-weight:bold; display:inline-block;">Xem phim ngay →</a>
                          </div>
                        </div>
                        <div style="background:#f9fafb; padding:16px; text-align:center; border-top:1px solid #e5e7eb;">
                          <p style="color:#9ca3af; font-size:12px; margin:0;">© 2025 CineHub. Hotline: 1800 6868 | Email: support@cinehub.vn</p>
                        </div>
                      </div>
                    </body>
                    </html>
                    """.formatted(fullName, paymentId, packageName, formattedAmount, formattedDate);

            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            // Log lỗi nhưng không throw để không ảnh hưởng luồng thanh toán
            System.err.println("Failed to send payment email: " + e.getMessage());
        }
    }

    /**
     * Gửi email OTP để đặt lại mật khẩu
     */
    @Async
    public void sendPasswordResetOtp(String toEmail, String fullName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🔑 CineHub - Mã xác minh đặt lại mật khẩu");

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head><meta charset="utf-8"></head>
                    <body style="font-family: Arial, sans-serif; background:#f4f4f4; padding:20px;">
                      <div style="max-width:600px; margin:0 auto; background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);">
                        <div style="background:#dc2626; padding:30px; text-align:center;">
                          <h1 style="color:#fff; margin:0; font-size:28px;">🎬 CineHub</h1>
                        </div>
                        <div style="padding:30px; text-align:center;">
                          <h2 style="color:#111;">Đặt lại mật khẩu</h2>
                          <p style="color:#444;">Xin chào <strong>%s</strong>,</p>
                          <p style="color:#444;">Sử dụng mã OTP bên dưới để đặt lại mật khẩu của bạn. Mã có hiệu lực trong <strong>10 phút</strong>.</p>
                          <div style="background:#fef2f2; border:2px dashed #dc2626; border-radius:12px; padding:24px; margin:24px 0; display:inline-block;">
                            <p style="margin:0; font-size:36px; font-weight:bold; letter-spacing:8px; color:#dc2626;">%s</p>
                          </div>
                          <p style="color:#9ca3af; font-size:13px;">Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>
                        </div>
                        <div style="background:#f9fafb; padding:16px; text-align:center; border-top:1px solid #e5e7eb;">
                          <p style="color:#9ca3af; font-size:12px; margin:0;">© 2025 CineHub. Hotline: 1800 6868</p>
                        </div>
                      </div>
                    </body>
                    </html>
                    """.formatted(fullName, otp);

            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }
    }
}
