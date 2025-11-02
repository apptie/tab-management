package com.management.tab.domain.repository;

import com.management.tab.domain.user.User;

public interface UserRepository {

    User save(User user);

    User find(Long userId);

    class UserNotFoundException extends IllegalArgumentException {

        public UserNotFoundException() {
            super("사용자를 찾을 수 없습니다.");
        }
    }
}
