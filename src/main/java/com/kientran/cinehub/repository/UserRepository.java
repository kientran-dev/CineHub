package com.kientran.cinehub.repository;

import com.kientran.cinehub.enums.UserRole;
import com.kientran.cinehub.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Tìm người dùng theo vai trò (ví dụ: tìm tất cả ADMIN hoặc MODERATOR)
    List<User> findByRolesContaining(UserRole role);

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

}
