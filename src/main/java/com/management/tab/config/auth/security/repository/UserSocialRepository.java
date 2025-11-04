package com.management.tab.config.auth.security.repository;

import com.management.tab.domain.user.User;
import com.management.tab.domain.user.vo.Social;
import java.util.Optional;

public interface UserSocialRepository {

    User save(User user);

    Optional<User> find(Social social);
}
