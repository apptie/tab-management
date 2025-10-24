package com.management.tab.application;

import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.repository.TabRepository;
import com.management.tab.domain.tab.Tab;
import com.management.tab.domain.tab.TabBuilder;
import com.management.tab.domain.tab.TabTree;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TabService {

    private final TabRepository tabRepository;

    @Transactional
    public TabId createRootTab(Long groupId, String title, String url) {
        TabPosition lastRootPosition = tabRepository.findLastRootPosition(groupId);
        Tab rootTab = TabBuilder.createRoot(groupId, title, url, lastRootPosition.next())
                                .build();

        return tabRepository.saveRoot(rootTab)
                            .id();
    }

    @Transactional
    public TabId createChildTab(Long parentId, String title, String url) {
        Tab parentTab = tabRepository.findTab(parentId);
        TabTree tabTree = tabRepository.findTabTree(parentTab.tabGroupId());

        tabTree.validateAddChildDepth(parentTab.id());

        TabPosition nextChildPosition = tabTree.getNextChildPosition(parentTab.id());
        Tab childTab = TabBuilder.createChild(parentTab, title, url, nextChildPosition)
                                 .build();

        return tabRepository.saveChild(childTab)
                            .id();
    }

    @Transactional
    public void deleteTab(Long tabId) {
        Tab tab = tabRepository.findTab(tabId);

        tabRepository.deleteTab(tab);
    }

    @Transactional
    public void deleteTabWithSubtree(Long tabId) {
        Tab tab = tabRepository.findTab(tabId);

        tabRepository.deleteTabWithSubtree(tab);
    }

    @Transactional
    public void moveRoot(Long tabId) {
        Tab tab = tabRepository.findTab(tabId);
        TabTree tabTree = tabRepository.findTabTree(tab.tabGroupId());
        TabPosition nextRootPosition = tabTree.getNextRootPosition();
        Tab movedTab = tab.moveToRoot(nextRootPosition);

        tabRepository.updateMovedRoot(movedTab, tab.parentId());
    }

    @Transactional
    public void moveRootWithSubtree(Long tabId) {
        Tab tab = tabRepository.findTab(tabId);
        TabTree tabTree = tabRepository.findTabTree(tab.tabGroupId());
        TabPosition nextRootPosition = tabTree.getNextRootPosition();
        Tab movedTab = tab.moveToRoot(nextRootPosition);

        tabRepository.updateMovedRootWithSubtree(movedTab);
    }

    @Transactional
    public void move(Long tabId, Long newParentId) {
        Tab tab = tabRepository.findTab(tabId);
        TabTree tabTree = tabRepository.findTabTree(tab.tabGroupId());

        tabTree.validateMove(tab.id(), TabId.create(newParentId));
        tabTree.validateMoveDepth(TabId.create(newParentId));

        TabPosition nextChildPosition = tabTree.getNextChildPosition(TabId.create(newParentId));
        Tab movedTab = tab.moveTo(TabId.create(newParentId), nextChildPosition);

        tabRepository.updateMoved(movedTab, tab.parentId());
    }

    @Transactional
    public void moveWithSubtree(Long tabId, Long newParentId) {
        Tab tab = tabRepository.findTab(tabId);
        TabTree tabTree = tabRepository.findTabTree(tab.tabGroupId());

        tabTree.validateMove(tab.id(), TabId.create(newParentId));
        tabTree.validateMoveDepthWithSubtree(tab.id(), TabId.create(newParentId));

        TabPosition nextChildPosition = tabTree.getNextChildPosition(TabId.create(newParentId));
        Tab movedTab = tab.moveTo(TabId.create(newParentId), nextChildPosition);

        tabRepository.updateMovedTabWithSubtree(movedTab);
    }

    @Transactional
    public void reorderTab(Long tabId, Long targetTabId, boolean after) {
        TabId movingParentId = tabRepository.findParentId(tabId);
        TabId targetParentId = tabRepository.findParentId(targetTabId);

        validateReorderTab(movingParentId, targetParentId);

        Tab movingTab = tabRepository.findTab(tabId);
        List<Tab> siblings = findMovingSiblings(movingParentId, movingTab);
        int targetIndex = findTabIndexInDomain(siblings, TabId.create(targetTabId));
        int insertIndex = after ? targetIndex + 1 : targetIndex;
        List<Tab> reorderedSiblings = new ArrayList<>(siblings);

        reorderedSiblings.add(insertIndex, movingTab);

        for (int i = 0; i < reorderedSiblings.size(); i++) {
            Tab sibling = reorderedSiblings.get(i);
            Tab updatedTab = sibling.updatePosition(i);

            tabRepository.updatePosition(updatedTab.id(), updatedTab.position());
        }
    }

    private void validateReorderTab(TabId movingParentId, TabId targetParentId) {
        boolean sameLevel = (movingParentId.isRoot() && targetParentId.isRoot())
                || (!movingParentId.isRoot() && movingParentId.equals(targetParentId));

        if (!sameLevel) {
            throw new IllegalArgumentException("같은 레벨의 탭만 순서를 변경할 수 있습니다");
        }
    }

    private List<Tab> findMovingSiblings(TabId movingParentId, Tab movingTab) {
        if (movingParentId.isRoot()) {
           return tabRepository.findRootSiblings()
                               .stream()
                               .filter(tab -> !tab.isEqualId(movingTab))
                               .toList();
        }

        return tabRepository.findSiblings(movingParentId)
                            .stream()
                            .filter(tab -> !tab.isEqualId(movingTab))
                            .toList();
    }

    private int findTabIndexInDomain(List<Tab> tabs, TabId targetId) {
        return IntStream.range(0, tabs.size())
                        .filter(i -> tabs.get(i).isEqualId(targetId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("대상 탭을 찾을 수 없습니다."));
    }

    @Transactional
    public void updateTab(Long tabId, String title, String url) {
        Tab tab = tabRepository.findTab(tabId);
        Tab updatedTab = tab.updateInfo(title, url);

        tabRepository.updateTabInfo(updatedTab);
    }

    public TabTree getTabTree(Long groupId) {
        return tabRepository.findTabTree(TabGroupId.create(groupId));
    }
}
