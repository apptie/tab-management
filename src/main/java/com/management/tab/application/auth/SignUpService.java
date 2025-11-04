package com.management.tab.application.auth;

import com.management.tab.config.auth.security.repository.UserSocialRepository;
import com.management.tab.domain.user.User;
import com.management.tab.domain.user.vo.Social;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class SignUpService {

    private final UserSocialRepository userSocialRepository;

    @Transactional
    public User signUp(Social social) {
        User user = User.create(social);

        return userSocialRepository.save(user);
    }
}
