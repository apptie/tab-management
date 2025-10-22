package com.management.tab.persistence.dao.dto;

import java.time.LocalDateTime;

public record TabGroupDto(Long id, String name, LocalDateTime createAt, LocalDateTime updatedAt) {
}
