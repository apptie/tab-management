package com.management.tab.domain.content;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.content.vo.Content;
import com.management.tab.domain.content.vo.TabContentId;
import com.management.tab.domain.tab.vo.TabId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "id")
public class TabContent {

    private final TabContentId id;
    private final TabId tabId;
    private final Content content;
    private final AuditTimestamps auditTimestamps;

    public static TabContent create(TabId tabId, String content) {
        return new TabContent(TabContentId.EMPTY_TAB_CONTENT_ID, tabId, Content.create(content), AuditTimestamps.now());
    }

    public static TabContent create(TabContentId id, TabId tabId, Content content, AuditTimestamps auditTimestamps) {
        return new TabContent(id, tabId, content, auditTimestamps);
    }

    private TabContent(TabContentId id, TabId tabId, Content content, AuditTimestamps auditTimestamps) {
        this.id = id;
        this.tabId = tabId;
        this.content = content;
        this.auditTimestamps = auditTimestamps;
    }

    public TabContent withId(TabContentId id){
        return new TabContent(
                id,
                this.tabId,
                this.content,
                this.auditTimestamps.updateTimestamp()
        );
    }

    public TabContent updateContent(String content) {
        return new TabContent(
                this.id,
                this.tabId,
                Content.create(content),
                this.auditTimestamps.updateTimestamp()
        );
    }
}
