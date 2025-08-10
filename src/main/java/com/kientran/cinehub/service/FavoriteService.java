package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.AddToFavoriteRequest;
import com.kientran.cinehub.dto.response.FavoriteResponse;
import com.kientran.cinehub.entity.Favorite;
import com.kientran.cinehub.exception.ContentNotFoundException;
import com.kientran.cinehub.mapper.FavoriteMapper;
import com.kientran.cinehub.repository.ContentRepository;
import com.kientran.cinehub.repository.FavoriteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteService {

    FavoriteRepository favoriteRepository;
    ContentRepository contentRepository;
    FavoriteMapper favoriteMapper;

    public Page<FavoriteResponse> getFavorites(String userId, Pageable pageable) {
        log.info("Lấy danh sách yêu thích cho user: {}", userId);

        Page<Favorite> favoritePage = favoriteRepository.findByUserIdAndIsFavoriteTrueOrderByCreatedAtDesc(userId, pageable);
        List<FavoriteResponse> responses = favoriteMapper.toResponseList(favoritePage.getContent());

        return new PageImpl<>(responses, pageable, favoritePage.getTotalElements());
    }

    public FavoriteResponse addToFavorite(String userId, AddToFavoriteRequest request) {
        log.info("Thêm vào yêu thích cho user: {}, content: {}", userId, request.getContentId());

        // Kiểm tra content có tồn tại không
        if (!contentRepository.existsById(request.getContentId())) {
            throw new ContentNotFoundException("Không tìm thấy nội dung với ID: " + request.getContentId());
        }

        // Kiểm tra đã yêu thích chưa
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndContentId(userId, request.getContentId());

        Favorite favorite;
        if (existingFavorite.isPresent()) {
            favorite = existingFavorite.get();
            favorite.setIsFavorite(true);
        } else {
            favorite = Favorite.builder()
                    .userId(userId)
                    .contentId(request.getContentId())
                    .isFavorite(true)
                    .build();
        }

        favorite = favoriteRepository.save(favorite);
        return favoriteMapper.toResponse(favorite);
    }

    public void removeFromFavorite(String userId, String contentId) {
        log.info("Xóa khỏi yêu thích cho user: {}, content: {}", userId, contentId);

        Optional<Favorite> favorite = favoriteRepository.findByUserIdAndContentId(userId, contentId);
        if (favorite.isPresent()) {
            favorite.get().setIsFavorite(false);
            favoriteRepository.save(favorite.get());
        }
    }

    public boolean isFavorite(String userId, String contentId) {
        return favoriteRepository.existsByUserIdAndContentIdAndIsFavoriteTrue(userId, contentId);
    }
}