package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.response.*;
import com.kientran.cinehub.entity.*;
import com.kientran.cinehub.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Hệ thống Khuyến nghị (Recommendation System) sử dụng Item-Based Collaborative Filtering.
 *
 * Nguyên lý:
 * - Xây dựng ma trận User-Movie từ dữ liệu: Rating (explicit) + WatchHistory & Favorite (implicit)
 * - Tính Cosine Similarity giữa các cặp phim dựa trên cách user đánh giá chúng
 * - Với mỗi phim mà user đã tương tác → tìm phim tương tự → dự đoán score → gợi ý
 *
 * Xử lý Cold Start:
 * - User mới (chưa có tương tác) → fallback sang phim phổ biến
 * - Phim mới (chưa ai tương tác) → không được gợi ý cho đến khi có dữ liệu
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationService {

    MovieRepository movieRepository;
    RatingRepository ratingRepository;
    WatchHistoryRepository watchHistoryRepository;
    FavoriteRepository favoriteRepository;
    UserRepository userRepository;
    MovieService movieService;

    // Điểm ngầm (implicit score) cho các hành vi khác nhau
    private static final double FAVORITE_SCORE = 4.5;
    private static final double WATCHED_SCORE = 3.5;
    private static final int TOP_K_SIMILAR = 10;     // Số phim tương tự tối đa để tính
    private static final int MAX_RECOMMENDATIONS = 12; // Số phim gợi ý tối đa

    /**
     * Gợi ý phim cho user dựa trên Collaborative Filtering.
     * Nếu user chưa có đủ dữ liệu (Cold Start) → trả phim phổ biến.
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> getRecommendationsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();

        // Bước 1: Xây ma trận User-Movie
        Map<Long, Map<Long, Double>> userMovieMatrix = buildUserMovieMatrix();

        // Bước 2: Lấy vector rating của user hiện tại
        Map<Long, Double> currentUserRatings = userMovieMatrix.get(userId);

        // Cold Start: user chưa có tương tác nào → trả phim phổ biến
        if (currentUserRatings == null || currentUserRatings.isEmpty()) {
            return getPopularMovies(MAX_RECOMMENDATIONS);
        }

        // Bước 3: Lấy tất cả movieId mà user đã tương tác
        Set<Long> interactedMovieIds = currentUserRatings.keySet();

        // Bước 4: Tính Cosine Similarity giữa các phim (Item-Based CF)
        // Lấy tất cả movieId trong hệ thống
        Set<Long> allMovieIds = userMovieMatrix.values().stream()
                .flatMap(m -> m.keySet().stream())
                .collect(Collectors.toSet());

        // Bước 5: Dự đoán score cho các phim user chưa xem
        Map<Long, Double> predictedScores = new HashMap<>();

        for (Long candidateMovieId : allMovieIds) {
            // Bỏ qua phim user đã tương tác
            if (interactedMovieIds.contains(candidateMovieId)) continue;

            double weightedSum = 0;
            double similaritySum = 0;

            // Với mỗi phim user đã xem, tính similarity với phim candidate
            for (Long interactedMovieId : interactedMovieIds) {
                double similarity = computeItemSimilarity(
                        interactedMovieId, candidateMovieId, userMovieMatrix);

                if (similarity > 0) {
                    weightedSum += similarity * currentUserRatings.get(interactedMovieId);
                    similaritySum += Math.abs(similarity);
                }
            }

            if (similaritySum > 0) {
                predictedScores.put(candidateMovieId, weightedSum / similaritySum);
            }
        }

        // Bước 6: Sắp xếp theo predicted score giảm dần, lấy top N
        List<Long> recommendedIds = predictedScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(MAX_RECOMMENDATIONS)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Cold Start partial: nếu gợi ý quá ít → bổ sung phim phổ biến
        if (recommendedIds.size() < MAX_RECOMMENDATIONS) {
            List<MovieResponse> popular = getPopularMovies(MAX_RECOMMENDATIONS);
            Set<Long> existingIds = new HashSet<>(recommendedIds);
            existingIds.addAll(interactedMovieIds);
            for (MovieResponse m : popular) {
                if (!existingIds.contains(m.getId()) && recommendedIds.size() < MAX_RECOMMENDATIONS) {
                    recommendedIds.add(m.getId());
                    existingIds.add(m.getId());
                }
            }
        }

        if (recommendedIds.isEmpty()) {
            return getPopularMovies(MAX_RECOMMENDATIONS);
        }

        // Bước 7: Fetch phim và trả kết quả
        List<Movie> movies = movieRepository.findAllByIdIn(recommendedIds);

        // Giữ đúng thứ tự predicted score
        Map<Long, Movie> movieMap = movies.stream()
                .collect(Collectors.toMap(Movie::getId, m -> m));

        return recommendedIds.stream()
                .filter(movieMap::containsKey)
                .map(id -> movieService.mapToResponse(movieMap.get(id)))
                .collect(Collectors.toList());
    }

    /**
     * Tìm phim tương tự cho một phim cụ thể (dùng cho trang Movie Detail).
     * Sử dụng Item-Based CF: tìm phim có pattern rating giống nhất.
     */
    @Transactional(readOnly = true)
    public List<MovieResponse> getSimilarMovies(Long movieId) {
        // Xây ma trận User-Movie
        Map<Long, Map<Long, Double>> userMovieMatrix = buildUserMovieMatrix();

        // Lấy tất cả movieId
        Set<Long> allMovieIds = userMovieMatrix.values().stream()
                .flatMap(m -> m.keySet().stream())
                .collect(Collectors.toSet());

        // Tính similarity giữa movieId và tất cả phim khác
        Map<Long, Double> similarities = new HashMap<>();
        for (Long otherId : allMovieIds) {
            if (otherId.equals(movieId)) continue;
            double sim = computeItemSimilarity(movieId, otherId, userMovieMatrix);
            if (sim > 0) {
                similarities.put(otherId, sim);
            }
        }

        // Sắp xếp theo similarity giảm dần
        List<Long> similarIds = similarities.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(MAX_RECOMMENDATIONS)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Nếu chưa đủ dữ liệu CF → fallback: lấy phim phổ biến (trừ phim hiện tại)
        if (similarIds.size() < 6) {
            List<MovieResponse> popular = getPopularMovies(MAX_RECOMMENDATIONS + 1);
            Set<Long> existingIds = new HashSet<>(similarIds);
            existingIds.add(movieId);
            for (MovieResponse m : popular) {
                if (!existingIds.contains(m.getId()) && similarIds.size() < MAX_RECOMMENDATIONS) {
                    similarIds.add(m.getId());
                    existingIds.add(m.getId());
                }
            }
        }

        if (similarIds.isEmpty()) {
            return getPopularMovies(MAX_RECOMMENDATIONS).stream()
                    .filter(m -> !m.getId().equals(movieId))
                    .limit(MAX_RECOMMENDATIONS)
                    .collect(Collectors.toList());
        }

        List<Movie> movies = movieRepository.findAllByIdIn(similarIds);
        Map<Long, Movie> movieMap = movies.stream()
                .collect(Collectors.toMap(Movie::getId, m -> m));

        return similarIds.stream()
                .filter(movieMap::containsKey)
                .map(id -> movieService.mapToResponse(movieMap.get(id)))
                .collect(Collectors.toList());
    }

    // ============================= PRIVATE METHODS =============================

    /**
     * Xây dựng ma trận User-Movie từ 3 nguồn dữ liệu:
     * 1. Rating (explicit) — score trực tiếp từ user
     * 2. Favorite (implicit) — score = 4.5
     * 3. WatchHistory (implicit) — score = 3.5
     *
     * Ma trận: Map<userId, Map<movieId, score>>
     * Nếu user vừa rating vừa favorite → ưu tiên rating (explicit)
     */
    private Map<Long, Map<Long, Double>> buildUserMovieMatrix() {
        Map<Long, Map<Long, Double>> matrix = new HashMap<>();

        // 1. Từ bảng Rating (explicit feedback — ưu tiên cao nhất)
        List<Rating> allRatings = ratingRepository.findAll();
        for (Rating r : allRatings) {
            matrix.computeIfAbsent(r.getUser().getId(), k -> new HashMap<>())
                    .put(r.getMovie().getId(), r.getScore());
        }

        // 2. Từ bảng Favorite (implicit feedback)
        List<Favorite> allFavorites = favoriteRepository.findAll();
        for (Favorite f : allFavorites) {
            matrix.computeIfAbsent(f.getUser().getId(), k -> new HashMap<>())
                    .putIfAbsent(f.getMovie().getId(), FAVORITE_SCORE); // Không ghi đè rating
        }

        // 3. Từ bảng WatchHistory (implicit feedback)
        List<WatchHistory> allHistory = watchHistoryRepository.findAll();
        for (WatchHistory wh : allHistory) {
            Long uId = wh.getUser().getId();
            Long mId = wh.getEpisodeVersion().getEpisode().getMovie().getId();
            matrix.computeIfAbsent(uId, k -> new HashMap<>())
                    .putIfAbsent(mId, WATCHED_SCORE); // Không ghi đè rating hoặc favorite
        }

        return matrix;
    }

    /**
     * Tính Cosine Similarity giữa 2 phim dựa trên vector rating từ tất cả user.
     *
     * Công thức:
     *                    Σ(Rᵤₐ × Rᵤᵦ)
     * sim(A, B) = ─────────────────────────────
     *              √(Σ Rᵤₐ²) × √(Σ Rᵤᵦ²)
     *
     * Trong đó u là các user đã rating CẢ HAI phim A và B.
     */
    private double computeItemSimilarity(Long movieA, Long movieB,
                                          Map<Long, Map<Long, Double>> userMovieMatrix) {
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        int commonUsers = 0;

        for (Map.Entry<Long, Map<Long, Double>> entry : userMovieMatrix.entrySet()) {
            Map<Long, Double> ratings = entry.getValue();
            Double ratingA = ratings.get(movieA);
            Double ratingB = ratings.get(movieB);

            // Chỉ tính khi user đã tương tác với CẢ HAI phim
            if (ratingA != null && ratingB != null) {
                dotProduct += ratingA * ratingB;
                normA += ratingA * ratingA;
                normB += ratingB * ratingB;
                commonUsers++;
            }
        }

        // Cần ít nhất 2 user chung để similarity có ý nghĩa
        if (commonUsers < 2) return 0;

        double denominator = Math.sqrt(normA) * Math.sqrt(normB);
        if (denominator == 0) return 0;

        return dotProduct / denominator;
    }

    /**
     * Lấy danh sách phim phổ biến (fallback cho Cold Start).
     * Sắp xếp theo: số lượng rating giảm dần, sau đó IMDb score giảm dần.
     */
    private List<MovieResponse> getPopularMovies(int limit) {
        return movieRepository.findPopularMovies().stream()
                .limit(limit)
                .map(movieService::mapToResponse)
                .collect(Collectors.toList());
    }
}
