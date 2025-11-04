package com.management.tab.config.auth.security.service;

import com.management.tab.config.auth.security.service.dto.LoggedInUserDto;
import com.management.tab.config.auth.security.repository.UserSocialRepository;
import com.management.tab.domain.user.User;
import com.management.tab.domain.user.vo.RegistrationId;
import com.management.tab.domain.user.vo.Social;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final SignUpService signUpService;
    private final UserSocialRepository userSocialRepository;

    public LoggedInUserDto login(String registrationIdName, String socialId) {
        RegistrationId registrationId = RegistrationId.findBy(registrationIdName);
        Social social = new Social(registrationId, socialId);

        return userSocialRepository.find(social)
                .map(user -> new LoggedInUserDto(user.getId(), user.getNickname(), false))
                .orElseGet(() -> signUp(social));
    }

    private LoggedInUserDto signUp(Social social) {
        User user = signUpService.signUp(social);

        return new LoggedInUserDto(user.getId(), "닉네임을 정해주세요.", true);
    }
}
