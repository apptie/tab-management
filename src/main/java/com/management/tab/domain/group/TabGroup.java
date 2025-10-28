package com.management.tab.domain.group;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.group.vo.TabGroupName;
import com.management.tab.domain.user.vo.UserId;
import java.time.LocalDateTime;

public class TabGroup {

    private final TabGroupId id;
    private final UserId creator;
    private final TabGroupName name;
    private final AuditTimestamps timestamps;

    public static TabGroup create(Long creatorId, String name) {
        return new TabGroup(
                TabGroupId.EMPTY_TAB_GROUP_ID,
                UserId.create(creatorId),
                TabGroupName.create(name),
                AuditTimestamps.now()
        );
    }

    public static TabGroup create(Long groupId, Long creatorId, String name, LocalDateTime createAt, LocalDateTime updatedAt) {
        return new TabGroup(
                TabGroupId.create(groupId),
                UserId.create(creatorId),
                TabGroupName.create(name),
                AuditTimestamps.create(createAt, updatedAt)
        );
    }

    private TabGroup(TabGroupId id, UserId creator, TabGroupName name, AuditTimestamps timestamps) {
        this.id = id;
        this.creator = creator;
        this.name = name;
        this.timestamps = timestamps;
    }

    public TabGroup updateAssignedId(Long id) {
        return new TabGroup(TabGroupId.create(id), this.creator, this.name, this.timestamps);
    }

    public TabGroup rename(String newName) {
        return new TabGroup(this.id, this.creator, TabGroupName.create(newName), this.timestamps);
    }

    public boolean isWriter(Long writerId) {
        return id.isEqualId(writerId);
    }

    public boolean isNotWriter(Long writerId) {
        return !isWriter(writerId);
    }

    public Long getId() {
        return id.getValue();
    }

    public Long getCreatorId() {
        return creator.getValue();
    }

    public String getName() {
        return name.getValue();
    }

    public LocalDateTime getCreatedAt() {
        return timestamps.getCreatedAt();
    }

    public LocalDateTime getUpdatedAt() {
        return timestamps.getUpdatedAt();
    }
}
