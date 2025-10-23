package com.management.tab.presentation.tab.dto.response;

import com.management.tab.domain.tab.Tab;
import com.management.tab.domain.tab.TabNode;
import com.management.tab.domain.tab.TabTree;
import java.util.List;
import lombok.Builder;

public record TabTreeResponse(List<TabNodeResponse> tabs) {

    public static TabTreeResponse from(TabTree tree) {
        List<TabNodeResponse> nodes = tree.getRootTabNodes()
                                          .stream()
                                          .map(TabNodeResponse::from)
                                          .toList();
        return new TabTreeResponse(nodes);
    }

    @Builder
    public record TabNodeResponse(
            Long id,
            Long parentId,
            String title,
            String url,
            Integer position,
            Integer depth,
            List<TabNodeResponse> children
    ) {

        public static TabNodeResponse from(TabNode node) {
            Tab tab = node.getTab();

            return TabNodeResponse.builder()
                                  .id(tab.getId().getValue())
                                  .parentId(tab.getParentId() != null ? tab.getParentId().getValue() : null)
                                  .title(tab.getTitle().getValue())
                                  .url(tab.getUrl().getValue())
                                  .position(tab.getPosition().getValue())
                                  .depth(node.getDepth())
                                  .children(
                                          node.getChildren()
                                              .stream()
                                              .map(TabNodeResponse::from)
                                              .toList()
                                  )
                                  .build();
        }
    }
}
