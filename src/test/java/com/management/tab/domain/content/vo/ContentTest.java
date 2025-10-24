package com.management.tab.domain.content.vo;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ContentTest {

    @Test
    void 유효한_문자열로_Content를_초기화할_수_있다() {
        // given
        String value = "테스트 내용";

        // when
        Content content = Content.create(value);

        // then
        assertAll(
                () -> assertThat(content).isNotNull(),
                () -> assertThat(content.getValue()).isEqualTo("테스트 내용")
        );
    }

    @ParameterizedTest(name = "{0}일 때 초기화에 실패한다")
    @NullAndEmptySource
    void 비어있는_문자열로_Content를_초기화할_수_없다(String value) {
        // when & then
        assertThatThrownBy(() -> Content.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content는 비어 있을 수 없습니다.");
    }

    @Test
    void 같은_값을_가진_Content는_동등하다() {
        // given
        Content content1 = Content.create("동일한 내용");
        Content content2 = Content.create("동일한 내용");

        // when & then
        assertAll(
                () -> assertThat(content1).isEqualTo(content2),
                () -> assertThat(content1).hasSameHashCodeAs(content2)
        );
    }

    @Test
    void 다른_값을_가진_Content는_동등하지_않다() {
        // given
        Content content1 = Content.create("첫 번째 내용");
        Content content2 = Content.create("두 번째 내용");

        // when & then
        assertAll(
                () -> assertThat(content1).isNotEqualTo(content2),
                () -> assertThat(content1).doesNotHaveSameHashCodeAs(content2)
        );
    }
}
