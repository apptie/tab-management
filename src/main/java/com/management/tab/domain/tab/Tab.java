package com.management.tab.domain.tab;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.tab.vo.GroupId;
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
    private final GroupId groupId;
    private final TabTitle title;
    private final TabUrl url;
    private final TabPosition position;
    private final AuditTimestamps timestamps;

    Tab(
            TabId id,
            TabId parentId,
            GroupId groupId,
            TabTitle title,
            TabUrl url,
            TabPosition position,
            AuditTimestamps timestamps
    ) {
        this.id = id;
        this.parentId = parentId;
        this.groupId = groupId;
        this.title = title;
        this.url = url;
        this.position = position;
        this.timestamps = timestamps;
    }

    public Tab updateInfo(String newTitle, String newUrl) {
        return new Tab(
                this.id,
                this.parentId,
                this.groupId,
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
                this.groupId,
                this.title,
                this.url,
                TabPosition.create(newPosition),
                this.timestamps.updateTimestamp()
        );
    }

    public Tab moveTo(TabId newParentId, int newPosition) {
        return new Tab(
                this.id,
                newParentId,
                this.groupId,
                this.title,
                this.url,
                TabPosition.create(newPosition),
                this.timestamps.updateTimestamp()
        );
    }

    public boolean isRoot() {
        return parentId == null;
    }

    public LocalDateTime getCreatedAt() {
        return timestamps.getCreatedAt();
    }

    public LocalDateTime getUpdatedAt() {
        return timestamps.getUpdatedAt();
    }
}
