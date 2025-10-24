package com.management.tab.presentation.content.dto.response;

import com.management.tab.domain.content.TabContent;
import java.util.List;

public record TabContentCollectionResponse(List<TabContentResponse> contents) {

    public static TabContentCollectionResponse from(List<TabContent> tabContents) {
        List<TabContentResponse> responses = tabContents.stream()
                                                        .map(TabContentResponse::from)
                                                        .toList();

        return new TabContentCollectionResponse(responses);
    }

    public record TabContentResponse(
            Long id,
            Long tabId,
            String content,
            String createdAt,
            String updatedAt
    ) {

        public static TabContentResponse from(TabContent tabContent) {
            return new TabContentResponse(
                    tabContent.getId().getValue(),
                    tabContent.getTabId().getValue(),
                    tabContent.getContent().getValue(),
                    tabContent.getAuditTimestamps().getCreatedAt().toString(),
                    tabContent.getAuditTimestamps().getUpdatedAt().toString()
            );
        }
    }
}
