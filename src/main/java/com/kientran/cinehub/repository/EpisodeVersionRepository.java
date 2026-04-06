package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.EpisodeVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpisodeVersionRepository extends JpaRepository<EpisodeVersion, Long> {
    List<EpisodeVersion> findByEpisodeId(Long episodeId);
    List<EpisodeVersion> findByEpisodeMovieId(Long movieId);
}
