package com.management.tab.persistence.dao;

import com.management.tab.persistence.dao.dto.UserDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/dao/insert-user-dao-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class UserDaoTest {

    @Autowired
    UserDao userDao;

    @Test
    void ID로_사용자를_조회할_수_있다() {
        // when
        Optional<UserDto> actual = userDao.findById(1L);

        // then
        assertAll(
                () -> assertThat(actual).isPresent(),
                () -> assertThat(actual.get().id()).isEqualTo(1L),
                () -> assertThat(actual.get().nickname()).isEqualTo("개발자")
        );
    }

    @Test
    void 존재하지_않는_ID로_조회하면_빈_Optional을_반환한다() {
        // when
        Optional<UserDto> actual = userDao.findById(999L);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 새로운_사용자를_저장할_수_있다() {
        // when
        Long actual = userDao.save("새로운 사용자", LocalDateTime.now(), LocalDateTime.now());

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).isPositive()
        );
    }

    @Test
    void 저장된_사용자를_조회할_수_있다() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long savedId = userDao.save("저장된 사용자", now, now);

        // when
        Optional<UserDto> actual = userDao.findById(savedId);

        // then
        assertAll(
                () -> assertThat(actual).isPresent(),
                () -> assertThat(actual.get().id()).isEqualTo(savedId),
                () -> assertThat(actual.get().nickname()).isEqualTo("저장된 사용자")
        );
    }
}
