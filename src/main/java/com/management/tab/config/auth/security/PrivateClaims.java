package com.management.tab.config.auth.security;

import java.time.LocalDateTime;

public record PrivateClaims(Long accountId, String roleName, LocalDateTime issuedAt) {
}
