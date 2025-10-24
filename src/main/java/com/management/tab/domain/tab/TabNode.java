package com.management.tab.domain.tab;

import com.management.tab.domain.tab.vo.TabId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "tab")
public class TabNode {
    
    private final Tab tab;
    private final int depth;
    private final List<TabNode> children;

    public static TabNode create(Tab tab, Integer depth) {
        return new TabNode(tab, depth, new ArrayList<>());
    }

    public static TabNode createRoot(Tab tab) {
        return new TabNode(tab, 0, new ArrayList<>());
    }

    private TabNode(Tab tab, int depth, List<TabNode> children) {
        this.tab = Objects.requireNonNull(tab, "Tab은 필수입니다");
        this.depth = depth;
        this.children = new ArrayList<>(children);
    }

    public void addChild(TabNode child) {
        validateChildAddition(child);
        this.children.add(child);
    }

    public void removeChild(TabId childId) {
        this.children.removeIf(child -> child.tab.isEqualId(childId));
    }

    public List<TabNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean hasChildren() {
        return !isLeaf();
    }

    public boolean isRoot() {
        return tab.isRoot();
    }

    public TabId getId() {
        return tab.id();
    }

    public int getPosition() {
        return tab.getPosition();
    }

    public TabId parentId() {
        return tab.parentId();
    }

    private void validateChildAddition(TabNode child) {
        if (child == null) {
            throw new IllegalArgumentException("자식 노드는 null일 수 없습니다.");
        }

        if (this.tab.isEqualId(child.tab)) {
            throw new IllegalArgumentException("자기 자신을 자식으로 추가할 수 없습니다.");
        }
    }
}
