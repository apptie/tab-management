package com.management.tab.tab.domain.embed;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OgTagTest {

    @Test
    void changeContent_메서드는_전달한_파라미터로_값을_변경한다() {
        // given
        OgTag ogTag = new OgTag("imageUrl", "title", "description");

        // when
        String expectedImageUrl = "changedImageUrl";
        String expectedTitle = "changedTitle";
        String expectedDescription = "changedDescription";

        OgTag actual = ogTag.changeContent(expectedImageUrl, expectedTitle, expectedDescription);

        // then
        assertThat(actual.getImageUrl()).isEqualTo(expectedImageUrl);
        assertThat(actual.getTitle()).isEqualTo(expectedTitle);
        assertThat(actual.getDescription()).isEqualTo(expectedDescription);
    }
}
