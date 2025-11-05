package com.management.tab.config.auth.resolver;

public record GuestUserId(Long userId) {

    public static final GuestUserId EMPTY_GUEST_ID = new GuestUserId(null);
}
