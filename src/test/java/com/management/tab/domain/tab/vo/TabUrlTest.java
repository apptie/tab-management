package com.management.tab.domain.tab.vo;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabUrlTest {

    @ParameterizedTest(name = "{0}로 TabUrl을 초기화할 수 있다")
    @ValueSource(strings = {
            "https://www.example.com",
            "http://example.com",
            "https://example.com/path",
            "https://example.com/path?query=value",
            "https://subdomain.example.com",
            "https://example.com:8080/path",
            "http://www.example.co.kr"
    })
    void 유효한_URL로_TabUrl을_초기화할_수_있다(String value) {
        // when
        TabUrl tabUrl = TabUrl.create(value);

        // then
        assertAll(
                () -> assertThat(tabUrl).isNotNull(),
                () -> assertThat(tabUrl.getValue()).isEqualTo(value)
        );
    }

    @ParameterizedTest(name = "{0}일 때 초기화에 실패한다")
    @NullAndEmptySource
    void 유효하지_않은_문자열으로는_TabUrl을_초기화할_수_없다(String value) {
        // when & then
        assertThatThrownBy(() -> TabUrl.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭 URL은 비어있을 수 없습니다");
    }

    @ParameterizedTest(name = "{0}로 TabUrl을 초기화할 수 없다")
    @ValueSource(strings = {
            "ftp://example.com",
            "example.com",
            "www.example.com",
            "https:/example.com",
            "https://",
            "http://",
            "://example.com",
            "https//example.com"
    })
    void 유효하지_않은_URL_형식으로는_TabUrl을_초기화할_수_없다(String value) {
        // when & then
        assertThatThrownBy(() -> TabUrl.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효한 URL 형식이 아닙니다");
    }

    @Test
    void 같은_값을_가진_TabUrl은_동등하다() {
        // given
        TabUrl tabUrl1 = TabUrl.create("https://example.com");
        TabUrl tabUrl2 = TabUrl.create("https://example.com");

        // when & then
        assertAll(
                () -> assertThat(tabUrl1).isEqualTo(tabUrl2),
                () -> assertThat(tabUrl1).hasSameHashCodeAs(tabUrl2)
        );
    }

    @Test
    void 다른_값을_가진_TabUrl은_동등하지_않다() {
        // given
        TabUrl tabUrl1 = TabUrl.create("https://example.com");
        TabUrl tabUrl2 = TabUrl.create("https://google.com");

        // when & then
        assertAll(
                () -> assertThat(tabUrl1).isNotEqualTo(tabUrl2),
                () -> assertThat(tabUrl1).doesNotHaveSameHashCodeAs(tabUrl2)
        );
    }
}
