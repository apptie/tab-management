package com.management.tab.persistence.dao.dto;

import com.management.tab.domain.user.User;
import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String nickname,
        String registrationId,
        String socialId,
        LocalDateTime createAt,
        LocalDateTime updatedAt
) {

    public User toUser() {
        return User.create(id, nickname, registrationId, socialId, createAt, updatedAt);
    }
}
