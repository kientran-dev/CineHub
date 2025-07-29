package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.UpdateProfileRequest;
import com.kientran.cinehub.dto.request.UserRegistrationRequest;
import com.kientran.cinehub.dto.response.UserResponse;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.enums.UserRole;
import com.kientran.cinehub.exception.EmailAlreadyExistsException;
import com.kientran.cinehub.exception.UserNotFoundException;
import com.kientran.cinehub.exception.InvalidAvatarException;
import com.kientran.cinehub.mapper.UserMapper;
import com.kientran.cinehub.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    AvatarService avatarService;

    /**
     * Đăng ký người dùng mới
     */
    public UserResponse registerNewUser(@Valid UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Tạo đối tượng User từ request
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Set avatar mặc định hoặc từ request
        user.setAvatarId(avatarService.getRandomAvatarId());


        user.getRoles().add(UserRole.USER); // Gán vai trò USER mặc định

        // Lưu người dùng vào cơ sở dữ liệu
        User savedUser = userRepository.save(user);

        // Sử dụng mapper để chuyển đổi
        return userMapper.toResponse(savedUser);
    }

    /**
     * Lấy thông tin người dùng theo email
     */
    public UserResponse getUserResponseByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return userMapper.toResponse(user);
    }

    /**
     * Lấy thông tin người dùng theo ID
     */
    public UserResponse getUserResponseById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        return userMapper.toResponse(user);
    }

    /**
     * Lấy thông tin profile của người dùng hiện tại
     */
    public Optional<UserResponse> getUserProfile() {
        // 1. Lấy thông tin xác thực từ Spring Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Kiểm tra xem người dùng đã được xác thực chưa
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        // 3. Lấy email (hoặc username) của người dùng từ thông tin xác thực
        String userEmail = authentication.getName();

        // 4. Dùng UserRepository để tìm kiếm User trong database dựa trên email và chuyển đổi bằng mapper
        return userRepository.findByEmail(userEmail)
                .map(userMapper::toResponse);
    }

    /**
     * Cập nhật thông tin profile của người dùng hiện tại
     */
    public UserResponse updateUserProfile(UpdateProfileRequest request) {
        // 1. Lấy email của người dùng đang đăng nhập từ Security Context
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Tìm User Entity trong database. Nếu không thấy, ném ra một ngoại lệ.
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng hoặc lỗi xác thực."));

        // 3. Cập nhật các trường thông tin
        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            currentUser.setFirstName(request.getFirstName().trim());
        }

        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            currentUser.setLastName(request.getLastName().trim());
        }

        // Kiểm tra và cập nhật avatar
        if (request.getAvatarId() != null) {
            if (avatarService.isValidAvatarId(request.getAvatarId())) {
                currentUser.setAvatarId(request.getAvatarId());
            } else {
                throw new InvalidAvatarException("Invalid avatar ID: " + request.getAvatarId());
            }
        }

        // 4. Lưu lại User Entity đã được cập nhật vào database
        User updatedUser = userRepository.save(currentUser);

        // 5. Sử dụng mapper để chuyển đổi Entity sang Response DTO
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Cập nhật avatar của người dùng hiện tại
     */
    public UserResponse updateUserAvatar(String avatarId) {
        // Lấy email của người dùng đang đăng nhập
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng hoặc lỗi xác thực."));

        // Kiểm tra avatar ID có hợp lệ không
        if (!avatarService.isValidAvatarId(avatarId)) {
            throw new InvalidAvatarException("Invalid avatar ID: " + avatarId);
        }

        currentUser.setAvatarId(avatarId);
        User savedUser = userRepository.save(currentUser);

        return userMapper.toResponse(savedUser);
    }

    /**
     * Kiểm tra email đã tồn tại chưa
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}