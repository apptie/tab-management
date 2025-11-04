package com.management.tab.persistence;

import com.management.tab.domain.repository.UserRepository;
import com.management.tab.domain.user.User;
import com.management.tab.persistence.dao.UserDao;
import com.management.tab.persistence.dao.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {

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
}
