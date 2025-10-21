package com.management.tab.domain.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
public class AuditTimestamps {

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static AuditTimestamps now() {
        LocalDateTime now = LocalDateTime.now();

        return new AuditTimestamps(now, now);
    }

    public AuditTimestamps updateTimestamp() {
        return new AuditTimestamps(this.createdAt, LocalDateTime.now());
    }

    private AuditTimestamps(LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

