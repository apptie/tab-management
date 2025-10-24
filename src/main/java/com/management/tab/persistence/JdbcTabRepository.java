package com.management.tab.persistence;

import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.repository.TabRepository;
import com.management.tab.domain.tab.Tab;
import com.management.tab.domain.tab.TabBuilder;
import com.management.tab.domain.tab.TabNode;
import com.management.tab.domain.tab.TabTree;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import com.management.tab.persistence.dao.DeleteTabDao;
import com.management.tab.persistence.dao.InsertTabDao;
import com.management.tab.persistence.dao.SelectTabDao;
import com.management.tab.persistence.dao.UpdateTabDao;
import com.management.tab.persistence.dao.dto.TabDto;
import com.management.tab.persistence.dao.dto.TabWithDepthDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcTabRepository implements TabRepository {

    private final InsertTabDao insertTabDao;
    private final SelectTabDao selectTabDao;
    private final UpdateTabDao updateTabDao;
    private final DeleteTabDao deleteTabDao;

    @Override
    public Tab saveRoot(Tab rootTab) {
        Long rootTabId = insertTabDao.saveRootTab(
                rootTab.getTabGroupId().getValue(),
                rootTab.getTitle().getValue(),
                rootTab.getUrl().getValue(),
                rootTab.getPosition().getValue(),
                rootTab.getCreatedAt(),
                rootTab.getUpdatedAt()
        );

        return TabBuilder.createWithAssignedId(rootTabId, rootTab)
                         .build();
    }

    @Override
    public Tab saveChild(Tab childTab) {
        Long childTabId = insertTabDao.saveChildTab(
                childTab.getTabGroupId().getValue(),
                childTab.getTitle().getValue(),
                childTab.getUrl().getValue(),
                childTab.getParentId().getValue(),
                childTab.getPosition().getValue(),
                childTab.getCreatedAt(),
                childTab.getUpdatedAt()
        );

        return TabBuilder.createWithAssignedId(childTabId, childTab)
                         .build();
    }

    @Override
    public Tab findTab(Long tabId) {
        return selectTabDao.findById(tabId)
                           .orElseThrow(() -> new IllegalArgumentException("지정한 탭을 찾을 수 없습니다."))
                           .toTab();
    }

    @Override
    public TabTree findTabTree(TabGroupId groupId) {
        List<TabWithDepthDto> tabWithDepthDtos = selectTabDao.findTreeByGroup(groupId.getValue());
        Map<Long, TabNode> nodeMap = new HashMap<>();
        List<TabNode> rootNodes = new ArrayList<>();

        for (TabWithDepthDto tabWithDepthDto : tabWithDepthDtos) {
            TabNode tabNode = tabWithDepthDto.toTabNode();

            nodeMap.put(tabNode.getId().getValue(), tabNode);

            if (tabNode.getParentId() == null) {
                rootNodes.add(tabNode);
            }
        }

        for (TabWithDepthDto dto : tabWithDepthDtos) {
            if (dto.hasParent()) {
                dto.findParentNode(nodeMap)
                   .ifPresent(
                           parent -> dto.findChildNode(nodeMap)
                                        .ifPresent(parent::addChild)
                   );
            }
        }

        return TabTree.create(groupId.getValue(), rootNodes);
    }

    @Override
    public TabId findParentId(Long tabId) {
        Long parentId = selectTabDao.findParentId(tabId)
                                    .orElse(null);

        if (parentId == null) {
            return null;
        }

        return TabId.create(parentId);
    }

    @Override
    public TabPosition findLastRootPosition(Long groupId) {
        int lastPosition = selectTabDao.findRootTabLastPosition(groupId);

        return TabPosition.create(lastPosition);
    }

    @Override
    public List<Tab> findSiblings(TabId parentId) {
        return selectTabDao.findSiblings(parentId.getValue())
                           .stream()
                           .map(TabDto::toTab)
                           .toList();
    }

    @Override
    public List<Tab> findRootSiblings() {
        return selectTabDao.findRootSiblings()
                           .stream()
                           .map(TabDto::toTab)
                           .toList();
    }

    @Override
    public void updateMoved(Tab movedTab, TabId currentParentId) {
        updateTabDao.updateMovingTabOnly(
                movedTab.getId().getValue(),
                currentParentId.getValue(),
                movedTab.getParentId().getValue()
        );
    }

    @Override
    public void updateMovedTabWithSubtree(Tab movedTab) {
        updateTabDao.updateMovingTabWithSubtree(movedTab.getId().getValue(), movedTab.getParentId().getValue());
    }

    @Override
    public void updateMovedRoot(Tab movedTab, TabId currentParentId) {
        updateTabDao.updateMovingTabOnly(movedTab.getId().getValue(), currentParentId.getValue(), null);
    }

    @Override
    public void updateMovedRootWithSubtree(Tab movedTab) {
        updateTabDao.updateMovingTabWithSubtree(movedTab.getId().getValue(), null);
    }

    @Override
    public void updatePosition(TabId id, TabPosition position) {
        updateTabDao.updatePosition(id.getValue(), position.getValue());
    }

    @Override
    public void updateTabInfo(Tab updatedTab) {
        updateTabDao.updateTab(
                updatedTab.getId().getValue(),
                updatedTab.getTitle().getValue(),
                updatedTab.getUrl().getValue()
        );
    }

    @Override
    public void deleteTabWithSubtree(Tab tab) {
        deleteTabDao.deleteTabWithSubtree(tab.getId().getValue());
    }

    @Override
    public void deleteTab(Tab tab) {
        deleteTabDao.deleteTabOnly(tab.getId().getValue(), tab.getParentId().getValue());
    }
}
