package com.management.tab.persistence.dao.dto;

import com.management.tab.domain.tab.Tab;
import com.management.tab.domain.tab.TabBuilder;
import com.management.tab.domain.tab.TabNode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public record TabWithDepthDto(
        Long id,
        Long groupId,
        Long parentId,
        Long writerId,
        String title,
        String url,
        int position,
        int depth,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public TabNode toTabNode() {
        TabBuilder builder = TabBuilder.builder()
                                       .groupId(this.groupId)
                                       .writerId(writerId)
                                       .title(this.title)
                                       .url(this.url)
                                       .position(this.position);

        if (this.id != null) {
            builder.id(this.id);
        }
        if (this.parentId != null) {
            builder.parentId(this.parentId);
        }

        Tab tab = builder.build();
        return TabNode.create(tab, this.depth);
    }

    public boolean hasParent() {
        return parentId != null;
    }

    public Optional<TabNode> findParentNode(Map<Long, TabNode> nodeMap) {
        return Optional.ofNullable(parentId)
                       .map(nodeMap::get);
    }

    public Optional<TabNode> findChildNode(Map<Long, TabNode> nodeMap) {
        return Optional.ofNullable(nodeMap.get(id));
    }
}
