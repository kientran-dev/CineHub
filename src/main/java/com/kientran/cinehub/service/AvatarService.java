package com.kientran.cinehub.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AvatarService {

    // Hardcode tạm thời, sau này sẽ move ra config
    private final String cloudName = "dv25wtndy"; // Thay bằng cloud name thật của bạn
    private final String avatarFolder = "avatars";

    // Danh sách các avatar có sẵn
    private final List<String> availableAvatars = Arrays.asList(
            "avatar_1", "avatar_2", "avatar_3", "avatar_4", "avatar_5",
            "avatar_6", "avatar_7", "avatar_8", "avatar_9"
    );

    /**
     * Tạo URL đầy đủ từ avatar ID
     */
    public String getAvatarUrl(String avatarId) {
        if (avatarId == null || avatarId.isEmpty()) {
            return getDefaultAvatarUrl();
        }
        return String.format("https://res.cloudinary.com/%s/image/upload/%s/%s.jpg",
                cloudName, avatarFolder, avatarId);
    }

    /**
     * Lấy URL avatar mặc định
     */
    public String getDefaultAvatarUrl() {
        return String.format("https://res.cloudinary.com/%s/image/upload/%s/avatar_1.jpg",
                cloudName, avatarFolder);
    }

    /**
     * Lấy danh sách tất cả avatar có sẵn với URL đầy đủ
     */
    public List<AvatarOption> getAvailableAvatars() {
        return availableAvatars.stream()
                .map(avatarId -> new AvatarOption(avatarId, getAvatarUrl(avatarId)))
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra avatar ID có hợp lệ không
     */
    public boolean isValidAvatarId(String avatarId) {
        return avatarId != null && availableAvatars.contains(avatarId);
    }

    /**
     * Lấy avatar ID ngẫu nhiên
     */
    public String getRandomAvatarId() {
        return availableAvatars.get(new Random().nextInt(availableAvatars.size()));
    }

    /**
     * Lấy danh sách avatar ID có sẵn
     */
    public List<String> getAvailableAvatarIds() {
        return availableAvatars;
    }

    @Data
    @AllArgsConstructor
    public static class AvatarOption {
        private String id;
        private String url;
    }
}