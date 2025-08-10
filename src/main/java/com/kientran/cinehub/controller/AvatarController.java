package com.kientran.cinehub.controller;

import com.kientran.cinehub.service.AvatarService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avatars")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AvatarController {

    AvatarService avatarService;

    /**
     * Lấy danh sách tất cả avatar có sẵn
     */
    @GetMapping
    public ResponseEntity<List<AvatarService.AvatarOption>> getAvailableAvatars() {
        List<AvatarService.AvatarOption> avatars = avatarService.getAvailableAvatars();
        return ResponseEntity.ok(avatars);
    }

    /**
     * Lấy danh sách avatar ID có sẵn
     */
    @GetMapping("/ids")
    public ResponseEntity<List<String>> getAvailableAvatarIds() {
        List<String> avatarIds = avatarService.getAvailableAvatarIds();
        return ResponseEntity.ok(avatarIds);
    }
}
