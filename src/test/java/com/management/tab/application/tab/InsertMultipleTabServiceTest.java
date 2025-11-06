package com.management.tab.application.tab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.management.tab.application.tab.dto.request.InsertMultipleTabRequest;
import com.management.tab.application.tab.dto.response.InsertMultipleTabResponse;
import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.repository.TabRepository;
import com.management.tab.domain.tab.TabTree;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/service/insert-multiple-tab-service-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class InsertMultipleTabServiceTest {

    @Autowired
    InsertMultipleTabService insertMultipleTabService;

    @Autowired
    TabRepository tabRepository;

    @Test
    void 루트_탭_여러_개를_생성할_수_있다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("루트 탭 1", "https://root1.com", Collections.emptyList()),
                new InsertMultipleTabRequest("루트 탭 2", "https://root2.com", Collections.emptyList())
        );

        // when
        List<InsertMultipleTabResponse> actual = insertMultipleTabService.insertMultipleRootTabs(1L, 1L, requests);

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    void 루트_탭_여러_개_생성_시_마지막_위치_다음에_추가된다() {
        // given
        TabTree beforeTree = tabRepository.findTabTree(TabGroupId.create(1L));
        int beforeCount = beforeTree.getTotalCount();

        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("새 루트 1", "https://new-root1.com", Collections.emptyList()),
                new InsertMultipleTabRequest("새 루트 2", "https://new-root2.com", Collections.emptyList())
        );

        // when
        insertMultipleTabService.insertMultipleRootTabs(1L, 1L, requests);

        // then
        TabTree actual = tabRepository.findTabTree(TabGroupId.create(1L));

        assertThat(actual.getTotalCount()).isEqualTo(beforeCount + 2);
    }

    @Test
    void 루트_탭_여러_개_생성_시_올바른_깊이를_갖는다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("루트 탭", "https://root.com", Collections.emptyList())
        );

        // when
        List<InsertMultipleTabResponse> actual = insertMultipleTabService.insertMultipleRootTabs(1L, 1L, requests);

        // then
        assertThat(actual.get(0).depth()).isZero();
    }

    @Test
    void 자식_탭_여러_개를_생성할_수_있다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("자식 탭 1", "https://child1.com", Collections.emptyList()),
                new InsertMultipleTabRequest("자식 탭 2", "https://child2.com", Collections.emptyList())
        );

        // when
        List<InsertMultipleTabResponse> actual = insertMultipleTabService.insertMultipleChildTabs(
                1L, 100L, 1L, requests
        );

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    void 자식_탭_여러_개_생성_시_마지막_위치_다음에_추가된다() {
        // given
        TabTree beforeTree = tabRepository.findTabTree(TabGroupId.create(1L));
        int beforeCount = beforeTree.getTotalCount();

        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("새 자식 1", "https://new-child1.com", Collections.emptyList()),
                new InsertMultipleTabRequest("새 자식 2", "https://new-child2.com", Collections.emptyList())
        );

        // when
        insertMultipleTabService.insertMultipleChildTabs(1L, 100L, 1L, requests);

        // then
        TabTree actual = tabRepository.findTabTree(TabGroupId.create(1L));

        assertThat(actual.getTotalCount()).isEqualTo(beforeCount + 2);
    }

    @Test
    void 자식_탭_여러_개_생성_시_올바른_깊이를_갖는다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("자식 탭", "https://child.com", Collections.emptyList())
        );

        // when
        List<InsertMultipleTabResponse> actual = insertMultipleTabService.insertMultipleChildTabs(
                1L, 100L, 1L, requests
        );

        // then
        assertThat(actual.get(0).depth()).isEqualTo(1);
    }

    @Test
    void 중첩된_루트_탭들을_생성할_수_있다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest(
                        "부모 루트",
                        "https://parent-root.com",
                        List.of(
                                new InsertMultipleTabRequest("자식 1", "https://child1.com", Collections.emptyList()),
                                new InsertMultipleTabRequest("자식 2", "https://child2.com", Collections.emptyList())
                        )
                )
        );

        // when
        List<InsertMultipleTabResponse> actual = insertMultipleTabService.insertMultipleRootTabs(1L, 1L, requests);

        // then
        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get(0).children()).hasSize(2),
                () -> assertThat(actual.get(0).depth()).isZero(),
                () -> assertThat(actual.get(0).children().get(0).depth()).isEqualTo(1)
        );
    }

    @Test
    void 깊은_중첩된_루트_탭들을_생성할_수_있다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest(
                        "레벨 1",
                        "https://level1.com",
                        List.of(
                                new InsertMultipleTabRequest(
                                        "레벨 2",
                                        "https://level2.com",
                                        List.of(
                                                new InsertMultipleTabRequest(
                                                        "레벨 3",
                                                        "https://level3.com",
                                                        Collections.emptyList()
                                                )
                                        )
                                )
                        )
                )
        );

        // when
        List<InsertMultipleTabResponse> actual = insertMultipleTabService.insertMultipleRootTabs(1L, 1L, requests);

        // then
        assertThat(actual.get(0).children()
                         .get(0).children()
                         .get(0).depth()).isEqualTo(2);
    }

    @Test
    void 중첩된_자식_탭들을_생성할_수_있다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest(
                        "자식 부모",
                        "https://child-parent.com",
                        List.of(
                                new InsertMultipleTabRequest("손자 1", "https://grandchild1.com", Collections.emptyList()),
                                new InsertMultipleTabRequest("손자 2", "https://grandchild2.com", Collections.emptyList())
                        )
                )
        );

        // when
        List<InsertMultipleTabResponse> actual = insertMultipleTabService.insertMultipleChildTabs(
                1L, 100L, 1L, requests
        );

        // then
        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get(0).children()).hasSize(2),
                () -> assertThat(actual.get(0).depth()).isEqualTo(1),
                () -> assertThat(actual.get(0).children().get(0).depth()).isEqualTo(2)
        );
    }

    @Test
    void 최대_깊이를_초과하면_루트_탭_여러_개를_생성할_수_없다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest(
                        "깊은 루트",
                        "https://deep-root.com",
                        List.of(
                                new InsertMultipleTabRequest(
                                        "깊은 레벨 1",
                                        "https://deep-level1.com",
                                        List.of(
                                                new InsertMultipleTabRequest(
                                                        "깊은 레벨 2",
                                                        "https://deep-level2.com",
                                                        List.of(
                                                                new InsertMultipleTabRequest(
                                                                        "깊은 레벨 3",
                                                                        "https://deep-level3.com",
                                                                        List.of(
                                                                                new InsertMultipleTabRequest(
                                                                                        "깊은 레벨 4",
                                                                                        "https://deep-level4.com",
                                                                                        List.of(
                                                                                                new InsertMultipleTabRequest(
                                                                                                        "깊은 레벨 5",
                                                                                                        "https://deep-level5.com",
                                                                                                        List.of(
                                                                                                                new InsertMultipleTabRequest(
                                                                                                                        "깊은 레벨 6",
                                                                                                                        "https://deep-level6.com",
                                                                                                                        List.of(
                                                                                                                                new InsertMultipleTabRequest(
                                                                                                                                        "깊은 레벨 7",
                                                                                                                                        "https://deep-level7.com",
                                                                                                                                        List.of(
                                                                                                                                                new InsertMultipleTabRequest(
                                                                                                                                                        "깊은 레벨 8",
                                                                                                                                                        "https://deep-level8.com",
                                                                                                                                                        List.of(
                                                                                                                                                                new InsertMultipleTabRequest(
                                                                                                                                                                        "깊은 레벨 9",
                                                                                                                                                                        "https://deep-level9.com",
                                                                                                                                                                        List.of(
                                                                                                                                                                                new InsertMultipleTabRequest(
                                                                                                                                                                                        "깊은 레벨 10",
                                                                                                                                                                                        "https://deep-level10.com",
                                                                                                                                                                                        Collections.emptyList()
                                                                                                                                                                                )
                                                                                                                                                                        )
                                                                                                                                                                )
                                                                                                                                                        )
                                                                                                                                                )
                                                                                                                                        )
                                                                                                                                )
                                                                                                                        )
                                                                                                                )
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        // when & then
        assertThatThrownBy(() -> insertMultipleTabService.insertMultipleRootTabs(1L, 1L, requests))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청의 최대 깊이(11)와 현재 깊이(0)를 합하면 최대 허용 깊이(10)를 초과합니다.");
    }

    @Test
    void 루트_탭_여러_개_생성_시_그룹_작성자가_아니면_실패한다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("루트 탭", "https://root.com", Collections.emptyList())
        );

        // when & then
        assertThatThrownBy(() -> insertMultipleTabService.insertMultipleRootTabs(1L, 999L, requests))
                .isInstanceOf(InsertMultipleTabService.BulkTabInsertForbiddenException.class)
                .hasMessage("탭 작성자가 아닙니다.");
    }

    @Test
    void 자식_탭_여러_개_생성_시_탭_그룹_작성자가_아니면_실패한다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("자식 탭", "https://child.com", Collections.emptyList())
        );

        // when & then
        assertThatThrownBy(() -> insertMultipleTabService.insertMultipleChildTabs(1L, 100L, 999L, requests))
                .isInstanceOf(InsertMultipleTabService.BulkTabInsertForbiddenException.class)
                .hasMessage("탭 작성자가 아닙니다.");
    }

    @Test
    void 요청_그룹의_탭이_아니면_자식_탭_여러_개를_생성할_수_없다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("자식 탭", "https://child.com", Collections.emptyList())
        );

        // when & then
        assertThatThrownBy(() -> insertMultipleTabService.insertMultipleChildTabs(2L, 100L, 1L, requests))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭 작성자가 아닙니다.");
    }

    @Test
    void 응답_구조에_탭_아이디가_포함된다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("루트 탭", "https://root.com", Collections.emptyList())
        );

        // when
        List<InsertMultipleTabResponse> actual = insertMultipleTabService.insertMultipleRootTabs(1L, 1L, requests);

        // then
        assertThat(actual.get(0).tabId()).isNotNull();
    }

    @Test
    void 응답_구조에_제목과_URL이_포함된다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("루트 탭", "https://root.com", Collections.emptyList())
        );

        // when
        List<InsertMultipleTabResponse> actual = insertMultipleTabService.insertMultipleRootTabs(1L, 1L, requests);

        // then
        assertAll(
                () -> assertThat(actual.get(0).title()).isEqualTo("루트 탭"),
                () -> assertThat(actual.get(0).url()).isEqualTo("https://root.com")
        );
    }

    @Test
    void 응답_구조에_깊이와_위치가_포함된다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("루트 탭", "https://root.com", Collections.emptyList())
        );

        // when
        List<InsertMultipleTabResponse> actual = insertMultipleTabService.insertMultipleRootTabs(1L, 1L, requests);

        // then
        assertAll(
                () -> assertThat(actual.get(0).depth()).isZero(),
                () -> assertThat(actual.get(0).position()).isGreaterThanOrEqualTo(0)
        );
    }

    @Test
    void 응답_구조에_자식_탭들이_포함된다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest(
                        "부모",
                        "https://parent.com",
                        List.of(
                                new InsertMultipleTabRequest("자식 1", "https://child1.com", Collections.emptyList()),
                                new InsertMultipleTabRequest("자식 2", "https://child2.com", Collections.emptyList())
                        )
                )
        );

        // when
        List<InsertMultipleTabResponse> actual = insertMultipleTabService.insertMultipleRootTabs(1L, 1L, requests);

        // then
        assertThat(actual.get(0).children()).hasSize(2);
    }

    @Test
    void 자식이_없으면_빈_리스트를_반환한다() {
        // given
        List<InsertMultipleTabRequest> requests = List.of(
                new InsertMultipleTabRequest("루트 탭", "https://root.com", Collections.emptyList())
        );

        // when
        List<InsertMultipleTabResponse> actual = insertMultipleTabService.insertMultipleRootTabs(1L, 1L, requests);

        // then
        assertThat(actual.get(0).children()).isEmpty();
    }
}
