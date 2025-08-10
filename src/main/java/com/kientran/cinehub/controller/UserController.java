package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.ChangePasswordRequest;
import com.kientran.cinehub.dto.request.UpdateProfileRequest;
import com.kientran.cinehub.dto.response.UserResponse;
import com.kientran.cinehub.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile() {
       return userService.getUserProfile()
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserResponse updatedUser = userService.updateUserProfile(request);
        return ResponseEntity.ok(updatedUser);
    }


}
