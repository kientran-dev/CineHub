package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.UpdateWatchProgressRequest;
import com.kientran.cinehub.dto.response.WatchProgressResponse;
import com.kientran.cinehub.entity.WatchProgress;
import com.kientran.cinehub.exception.ContentNotFoundException;
import com.kientran.cinehub.mapper.WatchProgressMapper;
import com.kientran.cinehub.repository.ContentRepository;
import com.kientran.cinehub.repository.WatchProgressRepository;
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
public class WatchProgressService {

    WatchProgressRepository watchProgressRepository;
    ContentRepository contentRepository;
    WatchProgressMapper watchProgressMapper;

    public Page<WatchProgressResponse> getContinueWatching(String userId, Pageable pageable) {
        log.info("Lấy danh sách tiếp tục xem cho user: {}", userId);

        Page<WatchProgress> progressPage = watchProgressRepository.findContinueWatchingByUserId(userId, pageable);
        List<WatchProgressResponse> responses = watchProgressMapper.toResponseList(progressPage.getContent());

        return new PageImpl<>(responses, pageable, progressPage.getTotalElements());
    }

    public WatchProgressResponse updateWatchProgress(String userId, UpdateWatchProgressRequest request) {
        log.info("Cập nhật tiến độ xem cho user: {}, content: {}", userId, request.getContentId());

        // Kiểm tra content có tồn tại không
        if (!contentRepository.existsById(request.getContentId())) {
            throw new ContentNotFoundException("Không tìm thấy nội dung với ID: " + request.getContentId());
        }

        // Tính phần trăm tiến độ
        double progressPercentage = (double) request.getCurrentTimeSeconds() / request.getTotalDurationSeconds() * 100;

        // Tìm progress hiện tại
        Optional<WatchProgress> existingProgress;
        if (request.getEpisodeId() != null) {
            existingProgress = watchProgressRepository.findByUserIdAndContentIdAndEpisodeId(
                    userId, request.getContentId(), request.getEpisodeId());
        } else {
            existingProgress = watchProgressRepository.findByUserIdAndContentId(userId, request.getContentId());
        }

        WatchProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCurrentTimeSeconds(request.getCurrentTimeSeconds());
            progress.setTotalDurationSeconds(request.getTotalDurationSeconds());
            progress.setProgressPercentage(progressPercentage);
            progress.setLastWatchedAt(LocalDateTime.now());
            progress.setIsCompleted(progressPercentage >= 90);

            // Cập nhật thông tin episode nếu có
            if (request.getSeasonId() != null) {
                progress.setSeasonId(request.getSeasonId());
            }
            if (request.getEpisodeId() != null) {
                progress.setEpisodeId(request.getEpisodeId());
            }
            if (request.getSeasonNumber() != null) {
                progress.setSeasonNumber(request.getSeasonNumber());
            }
            if (request.getEpisodeNumber() != null) {
                progress.setEpisodeNumber(request.getEpisodeNumber());
            }
        } else {
            progress = WatchProgress.builder()
                    .userId(userId)
                    .contentId(request.getContentId())
                    .seasonId(request.getSeasonId())
                    .episodeId(request.getEpisodeId())
                    .seasonNumber(request.getSeasonNumber())
                    .episodeNumber(request.getEpisodeNumber())
                    .currentTimeSeconds(request.getCurrentTimeSeconds())
                    .totalDurationSeconds(request.getTotalDurationSeconds())
                    .progressPercentage(progressPercentage)
                    .isCompleted(progressPercentage >= 90)
                    .lastWatchedAt(LocalDateTime.now())
                    .build();
        }

        // Nếu xem xong (>= 90%), xóa khỏi continue watching
        if (progressPercentage >= 90) {
            if (existingProgress.isPresent()) {
                watchProgressRepository.delete(progress);
            }
            return null; // Không trả về response vì đã xem xong
        }

        progress = watchProgressRepository.save(progress);
        return watchProgressMapper.toResponse(progress);
    }

    public void removeFromContinueWatching(String userId, String contentId) {
        log.info("Xóa khỏi tiếp tục xem cho user: {}, content: {}", userId, contentId);
        watchProgressRepository.deleteByUserIdAndContentId(userId, contentId);
    }
}