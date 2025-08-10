package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.WatchProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchProgressRepository extends MongoRepository<WatchProgress, String> {

    @Query("{ 'userId': ?0, 'progressPercentage': { $gt: 0, $lt: 90 } }")
    Page<WatchProgress> findContinueWatchingByUserId(String userId, Pageable pageable);

    @Query("{ 'userId': ?0, 'progressPercentage': { $gt: 0, $lt: 90 } }")
    List<WatchProgress> findContinueWatchingByUserId(String userId);

    Optional<WatchProgress> findByUserIdAndContentId(String userId, String contentId);

    @Query("{ 'userId': ?0, 'contentId': ?1, 'episodeId': ?2 }")
    Optional<WatchProgress> findByUserIdAndContentIdAndEpisodeId(String userId, String contentId, String episodeId);

    void deleteByUserIdAndContentId(String userId, String contentId);
}