package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.WatchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchHistoryRepository extends MongoRepository<WatchHistory, String> {
    Page<WatchHistory> findByUserIdOrderByLastWatchedAtDesc(String userId, Pageable pageable);

    List<WatchHistory> findByUserIdOrderByLastWatchedAtDesc(String userId);

    Optional<WatchHistory> findByUserIdAndMovieId(String userId, String movieId);

    void deleteByUserIdAndMovieId(String userId, String movieId);
}
