package com.management.tab.domain.content;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.content.vo.Content;
import com.management.tab.domain.content.vo.TabContentId;
import com.management.tab.domain.tab.vo.TabId;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "id")
public class TabContent {

    private final TabContentId id;
    private final TabId tabId;
    private final Content content;
    private final AuditTimestamps timestamps;

    public static TabContent create(TabId tabId, String content) {
        return new TabContent(TabContentId.EMPTY_TAB_CONTENT_ID, tabId, Content.create(content), AuditTimestamps.now());
    }

    public static TabContent create(TabContentId id, TabId tabId, Content content, AuditTimestamps auditTimestamps) {
        return new TabContent(id, tabId, content, auditTimestamps);
    }

    private TabContent(TabContentId id, TabId tabId, Content content, AuditTimestamps timestamps) {
        this.id = id;
        this.tabId = tabId;
        this.content = content;
        this.timestamps = timestamps;
    }

    public TabContent withId(TabContentId id){
        return new TabContent(
                id,
                this.tabId,
                this.content,
                this.timestamps.updateTimestamp()
        );
    }

    public TabContent updateContent(String content) {
        return new TabContent(
                this.id,
                this.tabId,
                Content.create(content),
                this.timestamps.updateTimestamp()
        );
    }

    public Long getId() {
        return id.getValue();
    }

    public Long getTabId() {
        return tabId.getValue();
    }

    public String getContent() {
        return content.getValue();
    }

    public LocalDateTime getCreatedAt() {
        return timestamps.getCreatedAt();
    }

    public LocalDateTime getUpdatedAt() {
        return timestamps.getUpdatedAt();
    }
}
