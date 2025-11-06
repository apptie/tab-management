package com.management.tab.domain.tab;

import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;

@Getter
public class TabTree {

    private static final int MAX_DEPTH = 10;

    private final TabGroupId tabGroupId;
    private final List<TabNode> rootTabNodes;
    private final Map<TabId, TabNode> tabNodeMap;

    public static TabTree create(Long groupId) {
        return TabTree.create(groupId, new ArrayList<>());
    }

    public static TabTree create(Long groupId, List<TabNode> rootNodes) {
        validateCreation(groupId, rootNodes);

        List<TabNode> unmodifiableRootNodes = Collections.unmodifiableList(rootNodes);
        Map<TabId, TabNode> unmodifiableTabNodes = buildNodeMap(rootNodes);

        return new TabTree(TabGroupId.create(groupId), unmodifiableRootNodes, unmodifiableTabNodes);
    }

    private static void validateCreation(Long groupId, List<TabNode> rootNodes) {
        Objects.requireNonNull(groupId, "탭 그룹 ID는 null일 수 없습니다.");
        Objects.requireNonNull(rootNodes, "루트 노드들은 null일 수 없습니다.");
    }

    private static Map<TabId, TabNode> buildNodeMap(List<TabNode> roots) {
        Map<TabId, TabNode> map = new HashMap<>();

        for (TabNode root : roots) {
            addToMap(root, map);
        }

        return Collections.unmodifiableMap(map);
    }

    private static void addToMap(TabNode node, Map<TabId, TabNode> map) {
        map.put(node.getId(), node);

        for (TabNode child : node.getChildren()) {
            addToMap(child, map);
        }
    }

    private TabTree(TabGroupId tabGroupId, List<TabNode> rootTabNodes, Map<TabId, TabNode> tabNodeMap) {
        this.tabGroupId = tabGroupId;
        this.rootTabNodes = rootTabNodes;
        this.tabNodeMap = tabNodeMap;
    }

    public void validateAddChildDepth(TabId parentId) {
        TabNode parentNode = findNode(parentId).orElseThrow(TabNodeNotFoundException::new);
        int currentDepth = parentNode.getDepth() + 1;

        validateDepth(currentDepth);
    }

    public void validateMove(TabId tabId, TabId newParentId) {
        if (tabId.equals(newParentId)) {
            throw new IllegalArgumentException("자기 자신을 부모로 설정할 수 없습니다.");
        }

        if (newParentId != null && isDescendant(tabId, newParentId)) {
            throw new IllegalArgumentException("순환 참조가 발생합니다: 자손을 부모로 설정할 수 없습니다.");
        }
    }

    public void validateMoveDepth(TabId newParentId) {
        TabNode newParentNode = findNode(newParentId).orElseThrow(TabNodeNotFoundException::new);
        int newParentDepth = newParentNode.getDepth() + 1;

        validateDepth(newParentDepth);
    }

    public void validateMoveDepthWithSubtree(TabId tabId, TabId newParentId) {
        TabNode newParentNode = findNode(newParentId).orElseThrow(TabNodeNotFoundException::new);
        TabNode movingNode = findNode(tabId).orElseThrow(TabNodeNotFoundException::new);
        int newMaxDepth = calculateAfterMovedDepth(newParentNode, movingNode);

        validateDepth(newMaxDepth);
    }

    public boolean isDescendant(TabId potentialAncestor, TabId potentialDescendant) {
        Optional<TabNode> ancestorNode = findNode(potentialAncestor);

        return ancestorNode.filter(tabNode -> containsDescendant(tabNode, potentialDescendant))
                           .isPresent();
    }

    public List<TabNode> findSiblings(TabId tabId) {
        return findNode(tabId).map(this::getSiblingsOf)
                              .orElse(Collections.emptyList());
    }

    public List<Tab> getAllTabs() {
        return tabNodeMap.values().stream()
                         .map(TabNode::getTab)
                         .toList();
    }

    public int getTotalCount() {
        return tabNodeMap.size();
    }

    public int getMaxDepth() {
        return tabNodeMap.values().stream()
                         .mapToInt(TabNode::getDepth)
                         .max()
                         .orElse(0);
    }

    public TabPosition getNextRootPosition() {
        if (rootTabNodes.isEmpty()) {
            return TabPosition.defaultPosition();
        }

        int position = rootTabNodes.stream()
                                   .mapToInt(TabNode::getPosition)
                                   .max()
                                   .orElse(-1) + 1;

        return TabPosition.create(position);
    }

    public TabPosition getNextChildPosition(TabId parentId) {
        TabNode parentNode = findNode(parentId).orElseThrow(TabNodeNotFoundException::new);

        if (parentNode.isLeaf()) {
            return TabPosition.defaultPosition();
        }

        return calculateNextChildPosition(parentNode);
    }

    public int findDepth(TabId tabId) {
        return findNode(tabId).map(TabNode::getDepth)
                              .orElseThrow(TabNodeNotFoundException::new);
    }

    public void validateCreateDepth(int requestTreeMaxDepth, int currentDepth) {
        int totalDepth = requestTreeMaxDepth + currentDepth;

        if (totalDepth > MAX_DEPTH) {
            throw new IllegalArgumentException(
                    "요청의 최대 깊이(%d)와 현재 깊이(%d)를 합하면 최대 허용 깊이(%d)를 초과합니다."
                            .formatted(requestTreeMaxDepth, currentDepth, MAX_DEPTH)
            );
        }
    }

    private Optional<TabNode> findNode(TabId tabId) {
        return Optional.ofNullable(tabNodeMap.get(tabId));
    }

    private void validateDepth(int targetDepth) {
        if (targetDepth >= MAX_DEPTH) {
            throw new IllegalArgumentException("탭의 계층은 " + MAX_DEPTH + "를 초과할 수 없습니다.");
        }
    }

    private boolean containsDescendant(TabNode node, TabId targetId) {
        return node.getChildren()
                   .stream()
                   .anyMatch(child -> child.getId().equals(targetId) || containsDescendant(child, targetId));
    }

    private int calculateAfterMovedDepth(TabNode newParentNode, TabNode movingNode) {
        int newParentDepth = newParentNode.getDepth();
        int subtreeDepth = calculateSubtreeDepth(movingNode);

        return newParentDepth + 1 + subtreeDepth;
    }

    private int calculateSubtreeDepth(TabNode node) {
        if (node.isLeaf()) {
            return 0;
        }

        int maxChildDepth = calculateMaxChildDepth(node);

        return maxChildDepth + 1;
    }

    private int calculateMaxChildDepth(TabNode node) {
        int maxChildDepth = 0;

        for (TabNode child : node.getChildren()) {
            int childDepth = calculateSubtreeDepth(child);
            maxChildDepth = Math.max(maxChildDepth, childDepth);
        }

        return maxChildDepth;
    }

    private List<TabNode> getSiblingsOf(TabNode node) {
        if (node.isRoot()) {
            return rootTabNodes;
        }

        return findNode(node.parentId()).map(TabNode::getChildren)
                                        .orElse(Collections.emptyList());
    }

    private TabPosition calculateNextChildPosition(TabNode parentNode) {
        int position = parentNode.getChildren()
                                 .stream()
                                 .mapToInt(TabNode::getPosition)
                                 .max()
                                 .orElse(-1) + 1;

        return TabPosition.create(position);
    }

    public static class TabNodeNotFoundException extends IllegalArgumentException {

        public TabNodeNotFoundException() {
            super("지정한 탭 노드를 찾을 수 없습니다.");
        }
    }
}
