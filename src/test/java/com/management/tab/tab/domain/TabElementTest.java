package com.management.tab.tab.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.management.tab.tab.domain.embed.OgTag;
import com.management.tab.tab.domain.embed.TabElementContent;
import com.management.tab.tab.domain.embed.TabElementHierarchy;
import com.management.tab.tab.domain.exception.InvalidOgTagContentException;
import com.management.tab.tab.domain.exception.InvalidTabElementDepthException;
import com.management.tab.tab.domain.exception.InvalidTabElementOrderException;
import com.management.tab.tab.domain.exception.InvalidTabElementUrlException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabElementTest {

    @Test
    void 정적_팩토리_메서드는_유효한_파라미터를_전달하면_TabElement를_생성한다() {
        // when & then
        TabElement actual = assertDoesNotThrow(
                () -> TabElement.of(
                        "탭 제목",
                        "https://탭 url",
                        "탭 설명",
                        false,
                        0,
                        0
                )
        );

        assertThat(actual).isInstanceOf(TabElement.class);
    }

    @Test
    void 정적_팩토리_메서드는_유효하지_않은_url을_전달하면_예외가_발생한다() {
        // when & then
        String invalidUrl = "invalidUrl";

        assertThatThrownBy(
                () -> TabElement.of(
                        "탭 제목",
                        invalidUrl,
                        "탭 설명",
                        false,
                        0,
                        0
                )
        ).isInstanceOf(InvalidTabElementUrlException.class);
    }

    @Test
    void 정적_팩토리_메서드는_유효하지_않은_order를_전달하면_예외가_발생한다() {
        // when & then
        int invalidOrder = -1;

        assertThatThrownBy(
                () -> TabElement.of(
                        "탭 제목",
                        "https://탭 url",
                        "탭 설명",
                        false,
                        invalidOrder,
                        0
                )
        ).isInstanceOf(InvalidTabElementOrderException.class);
    }

    @Test
    void 정적_팩토리_메서드는_유효하지_않은_depth를_전달하면_예외가_발생한다() {
        // when & then
        int invalidDepth = -1;

        assertThatThrownBy(
                () -> TabElement.of(
                        "탭 제목",
                        "https://탭 url",
                        "탭 설명",
                        false,
                        0,
                        invalidDepth
                )
        ).isInstanceOf(InvalidTabElementDepthException.class);
    }

    @Test
    void changeTabElementContent_메서드는_유효한_파라미터를_전달하면_TabElementContent를_변경한다() {
        // given
        TabElement tabElement = TabElement.of(
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        // when
        String expectedTitle = "변경된 탭 제목";
        String expectedUrl = "https://변경된 탭 url";
        String expectedDescription = "변경된 탭 설명";
        boolean expectedIsPublic = true;

        tabElement.changeTabElementContent(expectedTitle, expectedUrl, expectedDescription, expectedIsPublic);

        // then
        TabElementContent actual = tabElement.getTabElementContent();

        assertAll(
                () -> assertThat(actual.getTitle()).isEqualTo(expectedTitle),
                () -> assertThat(actual.getUrl()).isEqualTo(expectedUrl),
                () -> assertThat(actual.getDescription()).isEqualTo(expectedDescription),
                () -> assertThat(actual.isPublic()).isEqualTo(expectedIsPublic)
        );
    }

    @Test
    void changeTabElementContent_메서드는_유효하지_않은_url을_전달하면_예외가_발생한다() {
        // given
        TabElement tabElement = TabElement.of(
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        // when & then
        String invalidUrl = "invalidUrl";

        assertThatThrownBy(
                () -> tabElement.changeTabElementContent(
                        "변경된 탭 제목",
                        invalidUrl,
                        "변경된 탭 설명",
                        true
                )
        ).isInstanceOf(InvalidTabElementUrlException.class);
    }

    @Test
    void changeTabElementHierarchy_메서드는_유효한_파라미터를_전달하면_TabElementHierarchy를_변경한다() {
        // given
        TabElement tabElement = TabElement.of(
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        // when
        int expectedOrder = 1;
        int expectedDepth = 1;

        tabElement.changeTabElementHierarchy(expectedOrder, expectedDepth);

        // then
        TabElementHierarchy actual = tabElement.getTabElementHierarchy();

        assertAll(
                () -> assertThat(actual.getOrder()).isEqualTo(expectedOrder),
                () -> assertThat(actual.getDepth()).isEqualTo(expectedDepth)
        );
    }

    @Test
    void changeTabElementHierarchy_메서드는_유효하지_않은_order를_전달하면_예외가_발생한다() {
        // given
        TabElement tabElement = TabElement.of(
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        // when & then
        int invalidOrder = -1;

        assertThatThrownBy(() -> tabElement.changeTabElementHierarchy(invalidOrder, 1))
                .isInstanceOf(InvalidTabElementOrderException.class);
    }

    @Test
    void changeTabElementHierarchy_메서드는_유효하지_않은_depth를_전달하면_예외가_발생한다() {
        // given
        TabElement tabElement = TabElement.of(
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        // when & then
        int invalidDepth = -1;

        assertThatThrownBy(() -> tabElement.changeTabElementHierarchy(1, invalidDepth))
                .isInstanceOf(InvalidTabElementDepthException.class);
    }

    @Test
    void changeOgTag_메서드는_유효한_파라미터를_전달하면_OgTag를_변경한다() {
        // given
        TabElement tabElement = TabElement.of(
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        // when
        String expectedImageUrl = "https://변경된 og tag 이미지 url";
        String expectedTitle = "변경된 og tag 제목";
        String expectedDescription = "변경된 og tag 설명";

        tabElement.changeOgTag(expectedImageUrl, expectedTitle, expectedDescription);

        // then
        OgTag actual = tabElement.getOgTag();

        assertAll(
                () -> assertThat(actual.getImageUrl()).isEqualTo(expectedImageUrl),
                () -> assertThat(actual.getTitle()).isEqualTo(expectedTitle),
                () -> assertThat(actual.getDescription()).isEqualTo(expectedDescription)
        );
    }

    @ParameterizedTest(name = "imageUrl이 {0}일 때 예외가 발생한다")
    @NullAndEmptySource
    void changeOgTag_메서드는_유효하지_않은_imageUrl을_전달하면_예외가_발생한다(String invalidImageUrl) {
        // given
        TabElement tabElement = TabElement.of(
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        // when & then
        assertThatThrownBy(
                () -> tabElement.changeOgTag(
                        invalidImageUrl,
                        "변경된 og tag 제목",
                        "변경된 og tag 설명"
                )
        ).isInstanceOf(InvalidOgTagContentException.class);
    }

    @ParameterizedTest(name = "title이 {0}일 때 예외가 발생한다")
    @NullAndEmptySource
    void changeOgTag_메서드는_유효하지_않은_title을_전달하면_예외가_발생한다(String invalidTitle) {
        // given
        TabElement tabElement = TabElement.of(
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        // when & then
        assertThatThrownBy(
                () -> tabElement.changeOgTag(
                        "https://변경된 og tag 이미지 url",
                        invalidTitle,
                        "변경된 og tag 설명"
                )
        ).isInstanceOf(InvalidOgTagContentException.class);
    }

    @ParameterizedTest(name = "description이 {0}일 때 예외가 발생한다")
    @NullAndEmptySource
    void changeOgTag_메서드는_유효하지_않은_description을_전달하면_예외가_발생한다(String invalidDescription) {
        // given
        TabElement tabElement = TabElement.of(
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        // when & then
        assertThatThrownBy(
                () -> tabElement.changeOgTag(
                        "https://변경된 og tag 이미지 url",
                        "변경된 og tag 제목",
                        invalidDescription
                )
        ).isInstanceOf(InvalidOgTagContentException.class);
    }
}
