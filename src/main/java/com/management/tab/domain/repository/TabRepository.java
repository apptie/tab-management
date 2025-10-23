package com.management.tab.domain.repository;

import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.tab.Tab;
import com.management.tab.domain.tab.TabTree;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import java.util.List;

public interface TabRepository {

    Tab saveRoot(Tab rootTab);

    Tab saveChild(Tab childTab);

    Tab findTab(Long tabId);

    TabTree findTabTree(TabGroupId groupId);

    TabId findParentId(Long tabId);

    TabPosition findLastRootPosition(Long groupId);

    List<Tab> findSiblings(TabId parentId);

    List<Tab> findRootSiblings();

    void updateMoved(Tab movedTab, TabId currentParentId);

    void updateMovedTabWithSubtree(Tab movedTab);

    void updateMovedRoot(Tab movedTab, TabId currentParentId);

    void updateMovedRootWithSubtree(Tab movedTab);

    void updatePosition(TabId id, TabPosition position);

    void updateTabInfo(Tab updatedTab);

    void deleteTabWithSubtree(Tab tab);

    void deleteTab(Tab tab);
}
