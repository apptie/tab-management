package com.management.tab.tab.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.management.tab.config.util.stub.StubTabElement;
import com.management.tab.tab.domain.dto.HierarchyDto;
import com.management.tab.tab.domain.embed.OgTag;
import com.management.tab.tab.domain.embed.TabElementContent;
import com.management.tab.tab.domain.embed.TabElementHierarchy;
import com.management.tab.tab.domain.exception.AbsentTabElementException;
import com.management.tab.tab.domain.exception.AbsentTagException;
import com.management.tab.tab.domain.exception.InvalidHierarchySizeException;
import com.management.tab.tab.domain.exception.InvalidOgTagContentException;
import com.management.tab.tab.domain.exception.InvalidTabElementHierarchyException;
import com.management.tab.tab.domain.exception.InvalidTabElementUrlException;
import com.management.tab.tab.domain.exception.UnInitializedTabElementException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabGroupTest {

    @Test
    void addTabElement_메서드는_초기화된_tabElement를_전달하면_컬렉션에_저장한다() {
        // given
        TabGroup tabGroup = new TabGroup();
        TabElement tabElement = StubTabElement.of(
                1L,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        // when
        tabGroup.addTabElement(tabElement);

        // then
        Map<Long, TabElement> actual = tabGroup.getTabElements();

        assertThat(actual.size()).isEqualTo(1);
    }

    @Test
    void addTabElement_메서드는_초기화되지_않은_tabElement를_전달하면_예외가_발생한다() {
        // given
        TabGroup tabGroup = new TabGroup();
        TabElement tabElement = TabElement.of(
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        // when & then
        assertThatThrownBy(() -> tabGroup.addTabElement(tabElement))
                .isInstanceOf(UnInitializedTabElementException.class);
    }

    @Test
    void changeTabElementContent_메서드는_유효한_파라미터를_전달하면_tabElementConten_값을_변경한다() {
        // given
        Long id = 1L;
        TabElement tabElement = StubTabElement.of(
                id,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );
        TabGroup tabGroup = new TabGroup();

        tabGroup.addTabElement(tabElement);

        // when
        String expectedTitle = "변경된 탭 제목";
        String expectedUrl = "https://변경된 탭 url";
        String expectedDescription = "변경된 탭 설명";
        boolean expectedIsPublic = true;

        tabGroup.changeTabElementContent(id, expectedTitle, expectedUrl, expectedDescription, expectedIsPublic);

        // then
        TabElementContent actual = tabGroup.getTabElements().get(id).getTabElementContent();

        assertAll(
                () -> assertThat(actual.getTitle()).isEqualTo(expectedTitle),
                () -> assertThat(actual.getUrl()).isEqualTo(expectedUrl),
                () -> assertThat(actual.getDescription()).isEqualTo(expectedDescription),
                () -> assertThat(actual.isPublic()).isEqualTo(expectedIsPublic)
        );
    }

    @Test
    void changeTabElementContent_메서드는_유효하지_않은_id를_지정하면_예외가_발생한다() {
        // given
        TabGroup tabGroup = new TabGroup();

        // when & then
        Long invalidId = -1L;

        assertThatThrownBy(
                () -> tabGroup.changeTabElementContent(
                        invalidId,
                        "변경된 탭 제목",
                        "https://변경된 탭 url",
                        "변경된 탭 설명",
                        true
                )
        ).isInstanceOf(AbsentTabElementException.class);
    }

    @Test
    void changeTabElementContent_메서드는_유효하지_않은_url을_전달하면_예외가_발생한다() {
        // given
        Long id = 1L;
        TabElement tabElement = StubTabElement.of(
                id,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );
        TabGroup tabGroup = new TabGroup();

        tabGroup.addTabElement(tabElement);

        // when & then
        String invalidUrl = "invalidUrl";

        assertThatThrownBy(
                () -> tabGroup.changeTabElementContent(
                        id,
                        "변경된 탭 제목",
                        invalidUrl,
                        "변경된 탭 설명",
                        true
                )
        ).isInstanceOf(InvalidTabElementUrlException.class);
    }

    @Test
    void changeTabElementHierarchy_메서드는_유효한_파라미터를_전달하면_tabElementHierarchy_값을_변경한다() {
        // given
        TabGroup tabGroup = new TabGroup();
        Long id = 1L;
        TabElement tabElement = StubTabElement.of(
                id,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        tabGroup.addTabElement(tabElement);

        int expectedOrder = 1;
        int expectedDepth = 1;
        List<HierarchyDto> hierarchyDtos = List.of(new HierarchyDto(id, expectedOrder, expectedDepth));

        // when
        tabGroup.changeTabElementHierarchy(hierarchyDtos);

        // then
        TabElementHierarchy actual = tabGroup.getTabElements()
                                             .get(id)
                                             .getTabElementHierarchy();

        assertAll(
                () -> assertThat(actual.getOrder()).isEqualTo(expectedOrder),
                () -> assertThat(actual.getDepth()).isEqualTo(expectedDepth)
        );
    }

    @Test
    void changeTabElementHierarchy_메서드는_없는_식별자를_전달하면_예외가_발생한다() {
        // given
        TabGroup tabGroup = new TabGroup();
        List<HierarchyDto> hierarchyDtos = List.of(new HierarchyDto(1L, 1, 1));

        // when & then

        assertThatThrownBy(() -> tabGroup.changeTabElementHierarchy(hierarchyDtos))
                .isInstanceOf(InvalidHierarchySizeException.class);
    }

    @Test
    void changeTabElementHierarchy_메서드는_유효하지_않은_order를_전달하면_예외가_발생한다() {
        // given
        TabGroup tabGroup = new TabGroup();
        TabElement tabElement1 = StubTabElement.of(
                1L,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );
        TabElement tabElement2 = StubTabElement.of(
                2L,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                1,
                0
        );

        tabGroup.addTabElement(tabElement1);
        tabGroup.addTabElement(tabElement2);

        int invalidOrder = 3;

        List<HierarchyDto> invalidHierarchyDtos = List.of(
                new HierarchyDto(1L, 0, 0),
                new HierarchyDto(2L, invalidOrder, 0)
        );

        // when & then
        assertThatThrownBy(() -> tabGroup.changeTabElementHierarchy(invalidHierarchyDtos))
                .isInstanceOf(InvalidTabElementHierarchyException.class);
    }

    @Test
    void changeTabElementHierarchy_메서드는_유효하지_않은_depth를_전달하면_예외가_발생한다() {
        // given
        TabGroup tabGroup = new TabGroup();
        TabElement tabElement1 = StubTabElement.of(
                1L,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );
        TabElement tabElement2 = StubTabElement.of(
                2L,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                1,
                0
        );

        tabGroup.addTabElement(tabElement1);
        tabGroup.addTabElement(tabElement2);

        int invalidDepth = 3;

        List<HierarchyDto> invalidHierarchyDtos = List.of(
                new HierarchyDto(1L, 0, 0),
                new HierarchyDto(2L, 1, invalidDepth)
        );

        // when & then
        assertThatThrownBy(() -> tabGroup.changeTabElementHierarchy(invalidHierarchyDtos))
                .isInstanceOf(InvalidTabElementHierarchyException.class);
    }

    @Test
    void changeOgTag_메서드는_유효한_파라미터를_전달하면_ogTag_값을_변경한다() {
        // given
        TabGroup tabGroup = new TabGroup();
        Long id = 1L;
        TabElement tabElement = StubTabElement.of(
                id,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        tabGroup.addTabElement(tabElement);

        // when
        String expectedImageUrl = "https://변경된 og tag 이미지 url";
        String expectedTitle = "변경된 og tag 제목";
        String expectedDescription = "변경된 og tag 설명";

        tabGroup.changeOgTag(id, expectedImageUrl, expectedTitle, expectedDescription);

        // then
        OgTag actual = tabGroup.getTabElements()
                               .get(id)
                               .getOgTag();

        assertAll(
                () -> assertThat(actual.getImageUrl()).isEqualTo(expectedImageUrl),
                () -> assertThat(actual.getTitle()).isEqualTo(expectedTitle),
                () -> assertThat(actual.getDescription()).isEqualTo(expectedDescription)
        );
    }

    @Test
    void changeOgTag_메서드는_없는_식별자를_전달하면_예외가_발생한다() {
        // given
        TabGroup tabGroup = new TabGroup();
        TabElement tabElement = StubTabElement.of(
                1L,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        tabGroup.addTabElement(tabElement);

        // when & then
        Long invalidId = -1L;

        assertThatThrownBy(
                () -> tabGroup.changeOgTag(
                        invalidId,
                        "https://변경된 og tag 이미지 url",
                        "변경된 og tag 제목",
                        "변경된 og tag 설명"
                )
        ).isInstanceOf(AbsentTabElementException.class);
    }

    @ParameterizedTest(name = "imageUrl이 {0}일 때 예외가 발생한다")
    @NullAndEmptySource
    void changeOgTag_메서드는_유효하지_않은_imageUrl을_전달하면_예외가_발생한다(String invalidImageUrl) {
        // given
        TabGroup tabGroup = new TabGroup();
        Long id = 1L;
        TabElement tabElement = StubTabElement.of(
                id,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        tabGroup.addTabElement(tabElement);

        // when & then
        assertThatThrownBy(
                () -> tabGroup.changeOgTag(
                        id,
                        invalidImageUrl,
                        "변경된 og tag 제목",
                        "변경된 og tag 설명"
                )
        ).isInstanceOf(InvalidOgTagContentException.class);
    }

    @ParameterizedTest(name = "title이 {0}일 때 예외가 발생한다")
    @NullAndEmptySource
    void changeOgTag_메서드는_유효하지_않은_title을_전달하면_예외가_발생한다(String invalidTitle) {
        TabGroup tabGroup = new TabGroup();
        Long id = 1L;
        TabElement tabElement = StubTabElement.of(
                id,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        tabGroup.addTabElement(tabElement);

        // when & then
        assertThatThrownBy(
                () -> tabGroup.changeOgTag(
                        id,
                        "https://변경된 og tag 이미지 url",
                        invalidTitle,
                        "변경된 og tag 설명"
                )
        ).isInstanceOf(InvalidOgTagContentException.class);
    }

    @ParameterizedTest(name = "description이 {0}일 때 예외가 발생한다")
    @NullAndEmptySource
    void changeOgTag_메서드는_유효하지_않은_description을_전달하면_예외가_발생한다(String invalidDescription) {
        TabGroup tabGroup = new TabGroup();
        Long id = 1L;
        TabElement tabElement = StubTabElement.of(
                id,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        tabGroup.addTabElement(tabElement);

        // when & then
        assertThatThrownBy(
                () -> tabGroup.changeOgTag(
                        id,
                        "https://변경된 og tag 이미지 url",
                        "변경된 og tag 제목",
                        invalidDescription
                )
        ).isInstanceOf(InvalidOgTagContentException.class);
    }

    @Test
    void deleteTabElement_메서드는_식별자를_전달하면_해당_식별자에_해당하는_tabElement를_tabGroup의_컬렉션에서_삭제한다() {
        // given
        TabGroup tabGroup = new TabGroup();
        Long id = 1L;
        TabElement tabElement = StubTabElement.of(
                id,
                "탭 제목",
                "https://탭 url",
                "탭 설명",
                false,
                0,
                0
        );

        tabGroup.addTabElement(tabElement);

        // when
        tabGroup.deleteTabElement(id);

        // then
        Map<Long, TabElement> actual = tabGroup.getTabElements();

        assertThat(actual.size()).isZero();
    }

    @Test
    void deleteTabElement_메서드는_없는_식별자를_전달하면_예외가_발생한다() {
        // given
        TabGroup tabGroup = new TabGroup();

        // when & then
        Long invalidId = -1L;

        assertThatThrownBy(() -> tabGroup.deleteTabElement(invalidId))
                .isInstanceOf(AbsentTabElementException.class);
    }

    @Test
    void addTags_메서드는_전달한_태그_id를_tabGroup에_추가한다() {
        // given
        TabGroup tabGroup = new TabGroup();

        // when
        Long expectedTagId = 1L;

        tabGroup.addTags(List.of(expectedTagId));

        // then
        List<Long> actual = tabGroup.getTagIds();

        assertAll(
                () -> assertThat(actual.size()).isEqualTo(1),
                () -> assertThat(actual.get(0)).isEqualTo(expectedTagId)
        );
    }

    @Test
    void deleteTag_메서드는_파라미터로_전달한_태그를_tabGroup에서_삭제한다() {
        // given
        Long tagId = 1L;
        TabGroup tabGroup = new TabGroup();

        tabGroup.addTags(List.of(tagId));

        // when
        tabGroup.deleteTag(tagId);

        // then
        List<Long> actual = tabGroup.getTagIds();

        assertThat(actual.size()).isZero();
    }

    @Test
    void deleteTag_메서드는_tabGroup에_등록되지_않은_tabElement_id를_전달하면_예외가_발생한다() {
        // given
        TabGroup tabGroup = new TabGroup();

        // when & then
        Long invalidTagId = -1L;

        assertThatThrownBy(() -> tabGroup.deleteTag(invalidTagId)).isInstanceOf(AbsentTagException.class);
    }
}
