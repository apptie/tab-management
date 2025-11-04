package com.management.tab.domain.auth;

import java.time.LocalDateTime;

public record PrivateClaims(Long accountId, String roleName, LocalDateTime issuedAt) {
}
