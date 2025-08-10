package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.Favorite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends MongoRepository <Favorite, String> {
    Page<Favorite> findByUserIdAndIsFavoriteTrueOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<Favorite> findByUserIdAndIsFavoriteTrueOrderByCreatedAtDesc(String userId);

    Optional<Favorite> findByUserIdAndContentId(String userId, String contentId);

    boolean existsByUserIdAndContentIdAndIsFavoriteTrue(String userId, String contentId);

    void deleteByUserIdAndContentId(String userId, String contentId);
}
