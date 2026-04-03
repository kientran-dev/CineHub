package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.GenreRequest;
import com.kientran.cinehub.dto.response.GenreResponse;
import com.kientran.cinehub.entity.Genre;
import com.kientran.cinehub.repository.GenreRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreService {

    GenreRepository genreRepository;

    @Transactional
    public GenreResponse createGenre(GenreRequest request) {
        if (genreRepository.existsByName(request.getName())) {
            throw new RuntimeException("Thể loại này đã tồn tại");
        }
        Genre genre = Genre.builder()
                .name(request.getName())
                .build();
        return mapToResponse(genreRepository.save(genre));
    }

    public List<GenreResponse> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public GenreResponse getGenreById(Long id) {
        return genreRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại"));
    }

    @Transactional
    public GenreResponse updateGenre(Long id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại"));

        if (!genre.getName().equals(request.getName()) && genreRepository.existsByName(request.getName())) {
            throw new RuntimeException("Tên thể loại mới đã tồn tại");
        }

        genre.setName(request.getName());
        return mapToResponse(genreRepository.save(genre));
    }

    @Transactional
    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy thể loại để xóa");
        }
        // Lưu ý: Trong thực tế cần kiểm tra xem có phim nào đang thuộc thể loại này không trước khi xóa
        genreRepository.deleteById(id);
    }

    private GenreResponse mapToResponse(Genre genre) {
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }
}