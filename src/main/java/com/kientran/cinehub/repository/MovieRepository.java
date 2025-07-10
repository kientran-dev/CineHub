package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

    List<Movie> findByTitleContainingIgnoreCase(String title);

    // Có thể thêm các phương thức tìm kiếm khác theo nhu cầu:
    // List<Movie> findByReleaseDateBetween(LocalDate startDate, LocalDate endDate);
    // List<Movie> findByGenresName(String genreName); // Cần quan hệ phức tạp hơn
}
