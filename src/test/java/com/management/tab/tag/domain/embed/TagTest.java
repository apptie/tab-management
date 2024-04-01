package com.management.tab.tag.domain.embed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.management.tab.tag.domain.exception.InvalidTagNameException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TagTest {

    @Test
    void 빌더_패턴은_유효한_파라미터를_전달하면_Tag를_반환한다() {
        // when & then
        Tag actual = assertDoesNotThrow(
                () -> Tag.builder()
                         .name("태그")
                         .build()
        );

        assertThat(actual).isInstanceOf(Tag.class);
    }

    @ParameterizedTest(name = "{0}일 때 예외가 발생한다")
    @NullAndEmptySource
    void 빌더_패턴은_유효하지_않은_이름을_전달하면_예외가_발생한다(String invalidName) {
        // when & then
        assertThatThrownBy(
                () -> Tag.builder()
                         .name(invalidName)
                         .build()
        ).isInstanceOf(InvalidTagNameException.class);
    }

    @Test
    void addCount_메서드는_총_태그_개수와_공개된_태그_개수를_전달하면_시간_단위_태그_개수와_총_태그_개수를_더한다() {
        // given
        Tag tag = Tag.builder()
                     .name("태그")
                     .build();

        long addUnitTotalCount = 2L;
        long addUnitPublicCount = 1L;

        // when
        tag.addCount(addUnitTotalCount, addUnitPublicCount);

        // then
        long expectedUnitTotalCount = 2L;
        long expectedUnitPublicCount = 1L;
        long expectedTotalCount = 2L;
        long expectedPublicCount = 1L;

        assertThat(tag.getUnitTotalCount()).isEqualTo(expectedUnitTotalCount);
        assertThat(tag.getUnitPublicCount()).isEqualTo(expectedUnitPublicCount);
        assertThat(tag.getTotalCount()).isEqualTo(expectedTotalCount);
        assertThat(tag.getPublicCount()).isEqualTo(expectedPublicCount);
    }

    @Test
    void getPrivateCount_메서드는_호출하면_비공개된_태그의_개수를_계산해_반환한다() {
        // given
        Tag tag = Tag.builder()
                     .name("태그")
                     .build();

        long addUnitTotalCount = 2L;
        long addUnitPublicCount = 1L;

        tag.addCount(addUnitTotalCount, addUnitPublicCount);

        // when & then
        long expectedPrivateCount = 1L;

        assertThat(tag.getPrivateCount()).isEqualTo(expectedPrivateCount);
    }

    @Test
    void getUnitPrivateCount_메서드는_호출하면_비공개된_태그의_시간_단위_개수를_계산해_반환한다() {
        // given
        Tag tag = Tag.builder()
                     .name("태그")
                     .build();

        long addUnitTotalCount = 2L;
        long addUnitPublicCount = 1L;

        tag.addCount(addUnitTotalCount, addUnitPublicCount);

        // when & then
        long expectedUnitPrivateCount = 1L;

        assertThat(tag.getUnitPrivateCount()).isEqualTo(expectedUnitPrivateCount);
    }

    @Test
    void changeName_메서드는_파라미터에_이름을_전달하면_지정한_이름으로_변경한다() {
        // given
        Tag tag = Tag.builder()
                     .name("변경 전")
                     .build();

        // when
        String expectedName = "변경 후";

        tag.changeName(expectedName);

        // then
        assertThat(tag.getName()).isEqualTo(expectedName);
    }
}
