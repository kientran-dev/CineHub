package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.AddWatchHistoryRequest;
import com.kientran.cinehub.dto.response.WatchHistoryResponse;
import com.kientran.cinehub.entity.Content;
import com.kientran.cinehub.entity.WatchHistory;
import com.kientran.cinehub.exception.ContentNotFoundException;
import com.kientran.cinehub.mapper.WatchHistoryMapper;
import com.kientran.cinehub.repository.ContentRepository;
import com.kientran.cinehub.repository.WatchHistoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchHistoryService {

    WatchHistoryRepository watchHistoryRepository;
    ContentRepository contentRepository;
    WatchHistoryMapper watchHistoryMapper;

    public Page<WatchHistoryResponse> getWatchHistory(String userId, Pageable pageable) {
        log.info("Lấy lịch sử xem cho user: {}", userId);

        Page<WatchHistory> historyPage = watchHistoryRepository.findByUserIdOrderByLastWatchedAtDesc(userId, pageable);

        List<WatchHistoryResponse> responses = watchHistoryMapper.toResponseList(historyPage.getContent());

        return new PageImpl<>(responses, pageable, historyPage.getTotalElements());
    }

    public WatchHistoryResponse addToWatchHistory(String userId, AddWatchHistoryRequest request) {
        log.info("Thêm vào lịch sử xem cho user: {}, movie: {}", userId, request.getMovieId());

        // Kiểm tra content có tồn tại không
        Content content = contentRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ContentNotFoundException("Không tìm thấy nội dung với ID: " + request.getMovieId()));

        // Tính phần trăm xem
        Double watchPercentage = (double) request.getWatchedDurationSeconds() / request.getTotalDurationSeconds() * 100;

        // Kiểm tra xem đã có trong lịch sử chưa
        Optional<WatchHistory> existingHistory = watchHistoryRepository.findByUserIdAndMovieId(userId, request.getMovieId());

        WatchHistory watchHistory;
        if (existingHistory.isPresent()) {
            // Cập nhật lịch sử hiện tại
            watchHistory = existingHistory.get();
            watchHistory.setWatchedDurationSeconds(request.getWatchedDurationSeconds());
            watchHistory.setTotalDurationSeconds(request.getTotalDurationSeconds());
            watchHistory.setWatchPercentage(watchPercentage);
            watchHistory.setIsCompleted(watchPercentage >= 90);
            watchHistory.setLastWatchedAt(LocalDateTime.now());
        } else {
            // Tạo mới
            watchHistory = WatchHistory.builder()
                    .userId(userId)
                    .movieId(request.getMovieId())
                    .watchedDurationSeconds(request.getWatchedDurationSeconds())
                    .totalDurationSeconds(request.getTotalDurationSeconds())
                    .watchPercentage(watchPercentage)
                    .isCompleted(watchPercentage >= 90)
                    .lastWatchedAt(LocalDateTime.now())
                    .build();
        }

        watchHistory = watchHistoryRepository.save(watchHistory);
        return watchHistoryMapper.toResponse(watchHistory);
    }

    public void removeFromWatchHistory(String userId, String movieId) {
        log.info("Xóa khỏi lịch sử xem cho user: {}, movie: {}", userId, movieId);
        watchHistoryRepository.deleteByUserIdAndMovieId(userId, movieId);
    }
}