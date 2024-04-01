package com.management.tab.tag.domain.embed;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TagCounterTest {

    @Test
    void addCount_메서드는_총_태그_개수와_공개된_태그_개수를_전달하면_시간_단위_태그_개수와_총_태그_개수를_더한다() {
        // given
        TagCounter tagCounter = TagCounter.DEFAULT;

        long addUnitTotalCount = 2L;
        long addUnitPublicCount = 1L;

        // when
        TagCounter actual = tagCounter.addCount(addUnitTotalCount, addUnitPublicCount);

        // then
        long expectedUnitTotalCount = 2L;
        long expectedUnitPublicCount = 1L;
        long expectedTotalCount = 2L;
        long expectedPublicCount = 1L;

        assertThat(actual.getUnitTotalCount()).isEqualTo(expectedUnitTotalCount);
        assertThat(actual.getUnitPublicCount()).isEqualTo(expectedUnitPublicCount);
        assertThat(actual.getTotalCount()).isEqualTo(expectedTotalCount);
        assertThat(actual.getPublicCount()).isEqualTo(expectedPublicCount);
    }

    @Test
    void getPrivateCount_메서드는_호출하면_비공개된_태그의_개수를_계산해_반환한다() {
        // given
        TagCounter tagCounter = TagCounter.DEFAULT;

        long addUnitTotalCount = 2L;
        long addUnitPublicCount = 1L;

        TagCounter actual = tagCounter.addCount(addUnitTotalCount, addUnitPublicCount);

        // when & then
        long expectedPrivateCount = 1L;

        assertThat(actual.getPrivateCount()).isEqualTo(expectedPrivateCount);
    }

    @Test
    void getUnitPrivateCount_메서드는_호출하면_비공개된_태그의_시간_단위_개수를_계산해_반환한다() {
        // given
        TagCounter tagCounter = TagCounter.DEFAULT;

        long addUnitTotalCount = 2L;
        long addUnitPublicCount = 1L;

        TagCounter actual = tagCounter.addCount(addUnitTotalCount, addUnitPublicCount);

        // when & then
        long expectedUnitPrivateCount = 1L;

        assertThat(actual.getUnitPrivateCount()).isEqualTo(expectedUnitPrivateCount);
    }
}
