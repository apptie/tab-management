package com.management.tab.persistence.dao.dto;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.content.TabContent;
import com.management.tab.domain.content.vo.Content;
import com.management.tab.domain.content.vo.TabContentId;
import com.management.tab.domain.tab.vo.TabId;
import java.time.LocalDateTime;

public record TabContentDto(
        Long id,
        Long tabId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public TabContent toTabContent() {
        return TabContent.create(
                TabContentId.create(id),
                TabId.create(tabId),
                Content.create(content),
                AuditTimestamps.create(createdAt, updatedAt)
        );
    }
}
