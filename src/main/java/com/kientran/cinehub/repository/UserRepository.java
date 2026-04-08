package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query(value = "SELECT EXTRACT(MONTH FROM u.created_at) as month, EXTRACT(YEAR FROM u.created_at) as year, COUNT(u.id) as total " +
            "FROM users u " +
            "GROUP BY EXTRACT(YEAR FROM u.created_at), EXTRACT(MONTH FROM u.created_at) " +
            "ORDER BY EXTRACT(YEAR FROM u.created_at), EXTRACT(MONTH FROM u.created_at)", nativeQuery = true)
    List<Object[]> countUsersByMonth();
}