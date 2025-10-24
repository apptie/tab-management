package com.management.tab.presentation.content.dto.response;

import com.management.tab.domain.content.TabContent;

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
