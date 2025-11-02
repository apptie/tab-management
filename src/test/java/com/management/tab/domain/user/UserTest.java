package com.management.tab.domain.user;

import com.management.tab.domain.user.vo.UserId;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserTest {

    @Test
    void User를_이름과_소셜_로그인_정보로_초기화할_수_있다() {
        // when
        User actual = User.create("테스트 사용자", "KAKAO", "kakao12345");

        // then
        assertAll(
                () -> assertThat(actual.id()).isEqualTo(UserId.EMPTY_USER_ID),
                () -> assertThat(actual.getNickname()).isEqualTo("테스트 사용자")
        );
    }

    @Test
    void 모든_필드를_포함한_User를_초기화할_수_있다() {
        // given
        Long userId = 100L;
        String nickname = "테스트 사용자";
        String registrationId = "KAKAO";
        String socialId = "kakao12345";
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0);

        // when
        User actual = User.create(userId, nickname, registrationId, socialId, createdAt, updatedAt);

        // then
        assertAll(
                () -> assertThat(actual.id()).isEqualTo(UserId.create(userId)),
                () -> assertThat(actual.getNickname()).isEqualTo(nickname),
                () -> assertThat(actual.getRegistrationId()).isEqualTo(registrationId),
                () -> assertThat(actual.getSocialId()).isEqualTo(socialId),
                () -> assertThat(actual.getCreatedAt()).isEqualTo(createdAt),
                () -> assertThat(actual.getUpdatedAt()).isEqualTo(updatedAt)
        );
    }


    @Test
    void 기존_User에_id를_할당할_수_있다() {
        // given
        User existingUser = User.create("기존 사용자", "KAKAO", "kakao12345");

        // when
        User actual = existingUser.updateAssignedId(100L);

        // then
        assertThat(actual.id()).isEqualTo(UserId.create(100L));
    }

    @Test
    void User의_닉네임을_변경할_수_있다() {
        // given
        User user = User.create("원래 닉네임", "KAKAO", "kakao12345");

        // when
        User actual = user.changeNickname("변경된 닉네임");

        // then
        assertThat(actual.getNickname()).isEqualTo("변경된 닉네임");
    }
}
