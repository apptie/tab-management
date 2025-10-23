package com.management.tab.persistence.dao.dto;

import com.management.tab.domain.group.TabGroup;
import java.time.LocalDateTime;

public record TabGroupDto(Long id, String name, LocalDateTime createAt, LocalDateTime updatedAt) {

    public TabGroup toTabGroup() {
        return TabGroup.create(id, name, createAt, updatedAt);
    }
}
