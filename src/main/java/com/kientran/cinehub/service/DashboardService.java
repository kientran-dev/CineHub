package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.response.DashboardChartDTO;
import com.kientran.cinehub.dto.response.DashboardResponse;
import com.kientran.cinehub.entity.Payment;
import com.kientran.cinehub.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardService {
    UserRepository userRepository;
    MovieRepository movieRepository;
    PaymentRepository paymentRepository;
    PremiumSubscriptionRepository subscriptionRepository;
    GenreRepository genreRepository;
    WatchHistoryRepository watchHistoryRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalMovies = movieRepository.count();

        long premiumUsers = subscriptionRepository.findAll().stream()
                .filter(sub -> "ACTIVE".equals(sub.getStatus()) && sub.getEndDate() != null && sub.getEndDate().isAfter(LocalDateTime.now()))
                .map(sub -> sub.getUser().getId())
                .distinct()
                .count();

        BigDecimal totalRevenue = paymentRepository.findAll().stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()) && p.getAmount() != null)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalMovies(totalMovies)
                .premiumUsers(premiumUsers)
                .totalRevenue(totalRevenue)
                .build();
    }

    @Transactional(readOnly = true)
    public DashboardChartDTO getChartData() {
        List<DashboardChartDTO.MonthlyRevenue> revenueByMonth = buildRevenueByMonth();
        List<DashboardChartDTO.GenreCount> genreDistribution = buildGenreDistribution();
        List<DashboardChartDTO.DailyViews> viewsByDay = buildDailyViews();
        List<DashboardChartDTO.MonthlyRevenue> userGrowth = buildUserGrowth();

        return DashboardChartDTO.builder()
                .revenueByMonth(revenueByMonth)
                .genreDistribution(genreDistribution)
                .viewsByDay(viewsByDay)
                .userGrowthByMonth(userGrowth)
                .build();
    }

    private List<DashboardChartDTO.MonthlyRevenue> buildRevenueByMonth() {
        List<Object[]> rawRevenue = paymentRepository.findMonthlyRevenue();
        List<Object[]> rawUsers = userRepository.countUsersByMonth();

        // Nếu có dữ liệu, hiển thị theo năm/tháng thực tế
        // Nếu không có dữ liệu, trả về 12 tháng rỗng
        if (rawRevenue.isEmpty() && rawUsers.isEmpty()) {
            return buildEmptyMonths();
        }

        // Xây dựng map key = "year-month"
        Map<String, BigDecimal> revenueMap = new LinkedHashMap<>();
        for (Object[] row : rawRevenue) {
            int month = ((Number) row[0]).intValue();
            int year = ((Number) row[1]).intValue();
            BigDecimal total = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            revenueMap.put(year + "-" + month, total);
        }

        Map<String, Long> userMap = new LinkedHashMap<>();
        for (Object[] row : rawUsers) {
            int month = ((Number) row[0]).intValue();
            int year = ((Number) row[1]).intValue();
            long count = ((Number) row[2]).longValue();
            userMap.put(year + "-" + month, count);
        }

        // Tìm tất cả các key (year-month) và sắp xếp
        Set<String> allKeys = new TreeSet<>((a, b) -> {
            String[] pa = a.split("-");
            String[] pb = b.split("-");
            int cmp = Integer.compare(Integer.parseInt(pa[0]), Integer.parseInt(pb[0]));
            return cmp != 0 ? cmp : Integer.compare(Integer.parseInt(pa[1]), Integer.parseInt(pb[1]));
        });
        allKeys.addAll(revenueMap.keySet());
        allKeys.addAll(userMap.keySet());

        List<DashboardChartDTO.MonthlyRevenue> result = new ArrayList<>();
        for (String key : allKeys) {
            String[] parts = key.split("-");
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[0]);
            String label = "T" + month + "/" + year;

            result.add(DashboardChartDTO.MonthlyRevenue.builder()
                    .month(label)
                    .revenue(revenueMap.getOrDefault(key, BigDecimal.ZERO))
                    .users(userMap.getOrDefault(key, 0L))
                    .build());
        }
        return result;
    }

    private List<DashboardChartDTO.GenreCount> buildGenreDistribution() {
        List<Object[]> rawData = genreRepository.countMoviesByGenre();
        return rawData.stream()
                .map(row -> DashboardChartDTO.GenreCount.builder()
                        .name((String) row[0])
                        .value(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<DashboardChartDTO.DailyViews> buildDailyViews() {
        List<Object[]> rawData = watchHistoryRepository.countViewsLast7Days();

        // Tạo map từ kết quả query
        Map<LocalDate, Long> viewsMap = new LinkedHashMap<>();
        for (Object[] row : rawData) {
            LocalDate date;
            if (row[0] instanceof Date) {
                date = ((Date) row[0]).toLocalDate();
            } else {
                date = LocalDate.parse(row[0].toString());
            }
            long count = ((Number) row[1]).longValue();
            viewsMap.put(date, count);
        }

        // Tạo 7 ngày gần nhất
        List<DashboardChartDTO.DailyViews> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int dayOfWeek = date.getDayOfWeek().getValue(); // 1=Monday to 7=Sunday
            String dayName = dayOfWeek == 7 ? "CN" : "T" + (dayOfWeek + 1);

            result.add(DashboardChartDTO.DailyViews.builder()
                    .day(dayName + " (" + date.getDayOfMonth() + "/" + date.getMonthValue() + ")")
                    .views(viewsMap.getOrDefault(date, 0L))
                    .build());
        }
        return result;
    }

    private List<DashboardChartDTO.MonthlyRevenue> buildUserGrowth() {
        List<Object[]> rawData = userRepository.countUsersByMonth();

        if (rawData.isEmpty()) {
            return buildEmptyMonths();
        }

        List<DashboardChartDTO.MonthlyRevenue> result = new ArrayList<>();
        for (Object[] row : rawData) {
            int month = ((Number) row[0]).intValue();
            int year = ((Number) row[1]).intValue();
            long count = ((Number) row[2]).longValue();
            String label = "T" + month + "/" + year;

            result.add(DashboardChartDTO.MonthlyRevenue.builder()
                    .month(label)
                    .revenue(BigDecimal.ZERO)
                    .users(count)
                    .build());
        }
        return result;
    }

    private List<DashboardChartDTO.MonthlyRevenue> buildEmptyMonths() {
        List<DashboardChartDTO.MonthlyRevenue> result = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for (int m = 1; m <= 12; m++) {
            result.add(DashboardChartDTO.MonthlyRevenue.builder()
                    .month("T" + m + "/" + currentYear)
                    .revenue(BigDecimal.ZERO)
                    .users(0L)
                    .build());
        }
        return result;
    }

    @Transactional(readOnly = true)
    public String exportCsvReport() {
        List<Object[]> revenueRaw = paymentRepository.findMonthlyRevenue();
        List<Object[]> userRaw = userRepository.countUsersByMonth();

        // Build key-value maps
        Map<String, BigDecimal> revenueMap = new LinkedHashMap<>();
        for (Object[] row : revenueRaw) {
            int month = ((Number) row[0]).intValue();
            int year = ((Number) row[1]).intValue();
            BigDecimal total = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            revenueMap.put("T" + month + "/" + year, total);
        }

        Map<String, Long> userMap = new LinkedHashMap<>();
        for (Object[] row : userRaw) {
            int month = ((Number) row[0]).intValue();
            int year = ((Number) row[1]).intValue();
            long count = ((Number) row[2]).longValue();
            userMap.put("T" + month + "/" + year, count);
        }

        Set<String> allKeys = new LinkedHashSet<>();
        allKeys.addAll(revenueMap.keySet());
        allKeys.addAll(userMap.keySet());

        StringBuilder sb = new StringBuilder();
        sb.append("\uFEFF"); // BOM for UTF-8 Excel compatibility
        sb.append("Tháng,Doanh thu (VND),Người dùng mới\n");
        for (String key : allKeys) {
            sb.append(key).append(",")
              .append(revenueMap.getOrDefault(key, BigDecimal.ZERO)).append(",")
              .append(userMap.getOrDefault(key, 0L)).append("\n");
        }
        return sb.toString();
    }
}
