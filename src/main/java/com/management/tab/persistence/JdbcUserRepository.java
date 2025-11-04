package com.management.tab.persistence;

import com.management.tab.config.auth.security.repository.UserSocialRepository;
import com.management.tab.domain.repository.UserRepository;
import com.management.tab.domain.user.User;
import com.management.tab.domain.user.vo.Social;
import com.management.tab.persistence.dao.UserDao;
import com.management.tab.persistence.dao.dto.UserDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository, UserSocialRepository {

    private final UserDao userDao;

    @Override
    public User save(User user) {
        Long userId = userDao.save(
                user.getNickname(),
                user.getRegistrationId(),
                user.getSocialId(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

        return user.updateAssignedId(userId);
    }

    @Override
    public User find(Long userId) {
        return userDao.findById(userId)
                      .map(UserDto::toUser)
                      .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Optional<User> find(Social social) {
        return userDao.findBySocialInfo(social.registrationId().name(), social.socialId())
                      .map(UserDto::toUser);
    }
}
