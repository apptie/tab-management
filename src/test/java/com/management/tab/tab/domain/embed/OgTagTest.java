package com.management.tab.tab.domain.embed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.management.tab.tab.domain.exception.InvalidOgTagContentException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OgTagTest {

    @Test
    void changeContent_메서드는_유효한_파라미터를_전달하면_전달한_값으로_초기화한_OgTag를_변경한다() {
        // given
        OgTag ogTag = OgTag.DEFAULT;

        // when
        String expectedImageUrl = "changedImageUrl";
        String expectedTitle = "changedTitle";
        String expectedDescription = "changedDescription";

        OgTag actual = ogTag.changeContent(expectedImageUrl, expectedTitle, expectedDescription);

        // then
        assertAll(
                () -> assertThat(actual.getImageUrl()).isEqualTo(expectedImageUrl),
                () -> assertThat(actual.getTitle()).isEqualTo(expectedTitle),
                () -> assertThat(actual.getDescription()).isEqualTo(expectedDescription)
        );
    }

    @ParameterizedTest(name = "imageUrl이 {0}일 때 예외가 발생한다")
    @NullAndEmptySource
    void changeContent_메서드는_유효하지_않은_imageUrl을_전달하면_예외가_발생한다(String invalidImageUrl) {
        // given
        OgTag ogTag = OgTag.DEFAULT;

        // when & then
        assertThatThrownBy(() -> ogTag.changeContent(invalidImageUrl, "제목", "설명"))
                .isInstanceOf(InvalidOgTagContentException.class);
    }

    @ParameterizedTest(name = "title이 {0}일 때 예외가 발생한다")
    @NullAndEmptySource
    void changeContent_메서드는_유효하지_않은_title을_전달하면_예외가_발생한다(String invalidTitle) {
        // given
        OgTag ogTag = OgTag.DEFAULT;

        // when & then
        assertThatThrownBy(() -> ogTag.changeContent("이미지 링크", invalidTitle, "설명"))
                .isInstanceOf(InvalidOgTagContentException.class);
    }

    @ParameterizedTest(name = "description이 {0}일 때 예외가 발생한다")
    @NullAndEmptySource
    void changeContent_메서드는_유효하지_않은_description을_전달하면_예외가_발생한다(String invalidDescription) {
        // given
        OgTag ogTag = OgTag.DEFAULT;

        // when & then
        assertThatThrownBy(() -> ogTag.changeContent("이미지 링크", "제목", invalidDescription))
                .isInstanceOf(InvalidOgTagContentException.class);
    }
}
