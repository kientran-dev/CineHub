package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
    void deleteByUserIdAndMovieId(Long userId, Long movieId);

    @Query("SELECT f.movie.id FROM Favorite f WHERE f.user.id = :userId")
    List<Long> findFavoriteMovieIdsByUserId(Long userId);
}