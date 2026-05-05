package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findAllByIdIn(List<Long> ids);

    // Phim phổ biến: có nhiều rating nhất (fallback cho Cold Start)
    @Query("SELECT m FROM Movie m LEFT JOIN m.ratings r GROUP BY m ORDER BY COUNT(r) DESC, m.imdb DESC")
    List<Movie> findPopularMovies();
}