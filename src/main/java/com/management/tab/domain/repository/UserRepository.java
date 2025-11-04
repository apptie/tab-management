package com.management.tab.domain.repository;

import com.management.tab.domain.user.User;

public interface UserRepository {

    User save(User user);

    User find(Long userId);
}
