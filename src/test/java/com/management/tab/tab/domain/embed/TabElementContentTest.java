package com.management.tab.tab.domain.embed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.management.tab.tab.domain.exception.InvalidTabElementUrlException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabElementContentTest {

    @Test
    void 빌더_패턴으로_유효한_파라미터를_전달하면_TabElementContent를_생성한다() {
        // when & then
        TabElementContent actual = assertDoesNotThrow(
                () -> TabElementContent.builder()
                                       .title("제목")
                                       .url("https://")
                                       .description("설명")
                                       .isPublic(false)
                                       .build()

        );

        assertThat(actual).isInstanceOf(TabElementContent.class);
    }

    @Test
    void 빌더_패턴으로_title을_입력하지_않으면_url이_제목으로_초기화된다() {
        // given
        String expectedUrl = "https://";

        // when
        TabElementContent actual = TabElementContent.builder()
                                                    .url(expectedUrl)
                                                    .description("설명")
                                                    .isPublic(false)
                                                    .build();

        // then
        assertThat(actual.getTitle()).isEqualTo(expectedUrl);
    }

    @Test
    void 빌더_패턴으로_유효하지_않은_url을_입력하면_예외가_발생한다() {
        // given
        String invalidUrl = "invalidUrl";

        // when & then
        assertThatThrownBy(
                () -> TabElementContent.builder()
                                       .title("제목")
                                       .url(invalidUrl)
                                       .description("설명")
                                       .isPublic(false)
                                       .build()
        ).isInstanceOf(InvalidTabElementUrlException.class);
    }

    @Test
    void changeContent_메서드는_유효한_파라미터를_전달하면_변경된_값으로_초기화한_TabElementContent를_반환한다() {
        // given
        TabElementContent tabElementContent = TabElementContent.builder()
                                                               .title("변경 전 제목")
                                                               .url("https://이전")
                                                               .description("변경 전 설명")
                                                               .isPublic(false)
                                                               .build();

        // when
        String expectedTitle = "변경 후 제목";
        String expectedUrl = "https://이후";
        String expectedDescription = "변경 후 설명";
        boolean expectedIsPublic = true;

        TabElementContent actual = tabElementContent.changeContent(
                expectedTitle,
                expectedUrl,
                expectedDescription,
                expectedIsPublic
        );

        // then
        assertAll(
                () -> assertThat(actual.getTitle()).isEqualTo(expectedTitle),
                () -> assertThat(actual.getUrl()).isEqualTo(expectedUrl),
                () -> assertThat(actual.getDescription()).isEqualTo(expectedDescription),
                () -> assertThat(actual.isPublic()).isEqualTo(expectedIsPublic)
        );
    }

    @Test
    void changeContent_메서드는_유효하지_않은_url을_전달하면_예외가_발생한다() {
        // given
        TabElementContent tabElementContent = TabElementContent.builder()
                                                               .title("제목")
                                                               .url("https://")
                                                               .description("설명")
                                                               .isPublic(false)
                                                               .build();

        // when & then
        String invalidUrl = "invalidUrl";

        assertThatThrownBy(() -> tabElementContent.changeContent("제목", invalidUrl, "설명", true))
                .isInstanceOf(InvalidTabElementUrlException.class);
    }
}
