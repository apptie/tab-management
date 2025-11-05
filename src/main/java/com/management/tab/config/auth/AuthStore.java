package com.management.tab.config.auth;

import com.management.tab.domain.user.vo.UserId;
import org.springframework.stereotype.Component;

@Component
public class AuthStore {

    private final ThreadLocal<UserId> threadLocalAuthenticationStore = new ThreadLocal<>();

    public void set(UserId userInfo) {
        threadLocalAuthenticationStore.set(userInfo);
    }

    public UserId get() {
        return threadLocalAuthenticationStore.get();
    }

    public void remove() {
        threadLocalAuthenticationStore.remove();
    }
}

