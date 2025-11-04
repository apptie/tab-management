package com.management.tab.persistence;

import com.management.tab.domain.repository.UserRepository;
import com.management.tab.domain.user.User;
import com.management.tab.domain.user.vo.RegistrationId;
import com.management.tab.domain.user.vo.Social;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/jdbc/user-repository-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class JdbcUserRepositoryTest {

    @Autowired
    JdbcUserRepository jdbcUserRepository;

    @Test
    void 사용자를_저장할_수_있다() {
        // given
        User user = User.create("새로운 사용자", "KAKAO", "kakao12345");

        // when
        User actual = jdbcUserRepository.save(user);

        // then
        assertAll(
                () -> assertThat(actual.id()).isNotNull(),
                () -> assertThat(actual.getNickname()).isEqualTo("새로운 사용자"),
                () -> assertThat(actual.getRegistrationId()).isEqualTo("KAKAO"),
                () -> assertThat(actual.getSocialId()).isEqualTo("kakao12345")
        );
    }

    @Test
    void ID로_사용자를_조회할_수_있다() {
        // when
        User actual = jdbcUserRepository.find(1L);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(1L),
                () -> assertThat(actual.getNickname()).isEqualTo("테스트 사용자1")
        );
    }

    @Test
    void 존재하지_않는_ID로_조회하면_예외가_발생한다() {
        // given
        Long userId = -999L;

        // when & then
        assertThatThrownBy(() -> jdbcUserRepository.find(userId))
                .isInstanceOf(UserRepository.UserNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    void 소셜_정보로_사용자를_조회할_수_있다() {
        // given
        Social socialInfo = new Social(
                RegistrationId.findBy("KAKAO"),
                "kakao12345"
        );

        // when
        var actual = jdbcUserRepository.find(socialInfo);

        // then
        assertAll(
                () -> assertThat(actual).isPresent(),
                () -> assertThat(actual.get().getId()).isEqualTo(1L),
                () -> assertThat(actual.get().getNickname()).isEqualTo("테스트 사용자1"),
                () -> assertThat(actual.get().getRegistrationId()).isEqualTo("KAKAO"),
                () -> assertThat(actual.get().getSocialId()).isEqualTo("kakao12345")
        );
    }

    @Test
    void 존재하지_않는_소셜_정보로_조회하면_빈_Optional을_반환한다() {
        // given
        Social socialInfo = new Social(
                RegistrationId.findBy("KAKAO"),
                "naver-12345"
        );

        // when
        var actual = jdbcUserRepository.find(socialInfo);

        // then
        assertThat(actual).isEmpty();
    }
}
