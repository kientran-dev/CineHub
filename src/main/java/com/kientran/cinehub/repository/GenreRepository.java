package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    @Query(value = "SELECT g.name, COUNT(mg.movie_id) as movieCount " +
            "FROM genres g LEFT JOIN movie_genre mg ON g.id = mg.genre_id " +
            "GROUP BY g.id, g.name ORDER BY movieCount DESC", nativeQuery = true)
    List<Object[]> countMoviesByGenre();
}
