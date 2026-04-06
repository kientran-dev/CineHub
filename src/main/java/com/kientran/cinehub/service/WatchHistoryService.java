package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.WatchHistoryRequest;
import com.kientran.cinehub.dto.response.WatchHistoryResponse;
import com.kientran.cinehub.entity.EpisodeVersion;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.entity.WatchHistory;
import com.kientran.cinehub.repository.EpisodeVersionRepository;
import com.kientran.cinehub.repository.UserRepository;
import com.kientran.cinehub.repository.WatchHistoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchHistoryService {

    WatchHistoryRepository watchHistoryRepository;
    EpisodeVersionRepository episodeVersionRepository;
    UserRepository userRepository;

    @Transactional
    public WatchHistoryResponse saveWatchHistory(WatchHistoryRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        EpisodeVersion episodeVersion = episodeVersionRepository.findById(request.getEpisodeVersionId())
                .orElseThrow(() -> new RuntimeException("EpisodeVersion not found"));

        WatchHistory watchHistory = WatchHistory.builder()
                .user(user)
                .episodeVersion(episodeVersion)
                .watchTime(request.getWatchTime())
                .currentEpisode(request.getCurrentEpisode())
                .watchDate(LocalDateTime.now())
                .build();

        watchHistory = watchHistoryRepository.save(watchHistory);
        return mapToResponse(watchHistory);
    }

    public List<WatchHistoryResponse> getMyWatchHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return watchHistoryRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteWatchHistory(Long id, String username) {
        WatchHistory history = watchHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("History not found"));

        if (!history.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You do not have permission to delete this history");
        }

        watchHistoryRepository.delete(history);
    }

    private WatchHistoryResponse mapToResponse(WatchHistory history) {
        EpisodeVersion version = history.getEpisodeVersion();
        return WatchHistoryResponse.builder()
                .id(history.getId())
                .episodeVersionId(version.getId())
                .episodeId(version.getEpisode().getId())
                .movieTitle(version.getEpisode().getMovie().getTitle())
                .episodeName(version.getEpisode().getEpisodeName())
                .versionType(version.getType())
                .watchTime(history.getWatchTime())
                .currentEpisode(history.getCurrentEpisode())
                .watchDate(history.getWatchDate())
                .build();
    }
}