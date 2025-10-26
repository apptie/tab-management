package com.management.tab.persistence.dao.dto;

import java.time.LocalDateTime;

public record UserDto(Long id, String nickname, LocalDateTime createAt, LocalDateTime updatedAt) {
}
