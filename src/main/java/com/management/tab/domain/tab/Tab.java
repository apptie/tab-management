package com.management.tab.domain.tab;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import com.management.tab.domain.tab.vo.TabTitle;
import com.management.tab.domain.tab.vo.TabUrl;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = {"id"})
public class Tab {

    private final TabId id;
    private final TabId parentId;
    private final TabGroupId tabGroupId;
    private final TabTitle title;
    private final TabUrl url;
    private final TabPosition position;
    private final AuditTimestamps timestamps;

    Tab(
            TabId id,
            TabId parentId,
            TabGroupId tabGroupId,
            TabTitle title,
            TabUrl url,
            TabPosition position,
            AuditTimestamps timestamps
    ) {
        this.id = id;
        this.parentId = parentId;
        this.tabGroupId = tabGroupId;
        this.title = title;
        this.url = url;
        this.position = position;
        this.timestamps = timestamps;
    }

    public Tab updateAssignedId(Long tabId) {
        return new Tab(
                TabId.create(tabId),
                this.parentId,
                this.tabGroupId,
                this.title,
                this.url,
                this.position,
                this.timestamps
        );
    }

    public Tab updateInfo(String newTitle, String newUrl) {
        return new Tab(
                this.id,
                this.parentId,
                this.tabGroupId,
                TabTitle.create(newTitle),
                TabUrl.create(newUrl),
                this.position,
                this.timestamps.updateTimestamp()
        );
    }

    public Tab updatePosition(int newPosition) {
        return new Tab(
                this.id,
                this.parentId,
                this.tabGroupId,
                this.title,
                this.url,
                TabPosition.create(newPosition),
                this.timestamps.updateTimestamp()
        );
    }

    public Tab moveTo(TabId newParentId, TabPosition newPosition) {
        return new Tab(
                this.id,
                newParentId,
                this.tabGroupId,
                this.title,
                this.url,
                newPosition,
                this.timestamps.updateTimestamp()
        );
    }

    public Tab moveToRoot(TabPosition newPosition) {
        return new Tab(
                this.id,
                null,
                this.tabGroupId,
                this.title,
                this.url,
                newPosition,
                this.timestamps.updateTimestamp()
        );
    }

    public boolean isRoot() {
        return parentId == null;
    }

    public boolean isEqualId(Tab other) {
        return this.id.equals(other.id);
    }

    public boolean isEqualTo(TabId id) {
        return this.id.equals(id);
    }

    public LocalDateTime getCreatedAt() {
        return timestamps.getCreatedAt();
    }

    public LocalDateTime getUpdatedAt() {
        return timestamps.getUpdatedAt();
    }
}
