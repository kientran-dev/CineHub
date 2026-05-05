package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    List<WatchHistory> findByUserId(Long userId);

    @Query("SELECT wh FROM WatchHistory wh WHERE wh.user.id = :userId AND wh.episodeVersion.episode.movie.id = :movieId")
    java.util.Optional<WatchHistory> findByUserIdAndMovieId(Long userId, Long movieId);

    @Query(value = "SELECT CAST(w.watch_date AS DATE) as watchDay, COUNT(w.id) as total " +
            "FROM watch_histories w WHERE w.watch_date >= CURRENT_DATE - INTERVAL '7 days' " +
            "GROUP BY CAST(w.watch_date AS DATE) ORDER BY CAST(w.watch_date AS DATE)", nativeQuery = true)
    List<Object[]> countViewsLast7Days();

    @Query("SELECT DISTINCT wh.episodeVersion.episode.movie.id FROM WatchHistory wh WHERE wh.user.id = :userId")
    List<Long> findWatchedMovieIdsByUserId(Long userId);

    @Query("SELECT wh FROM WatchHistory wh WHERE wh.episodeVersion.episode.movie.id = :movieId")
    List<WatchHistory> findByMovieId(Long movieId);
}