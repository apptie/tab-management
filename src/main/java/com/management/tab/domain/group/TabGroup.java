package com.management.tab.domain.group;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.group.vo.TabGroupName;
import com.management.tab.domain.user.vo.UserId;
import java.time.LocalDateTime;

public class TabGroup {

    private final TabGroupId id;
    private final UserId writer;
    private final TabGroupName name;
    private final AuditTimestamps timestamps;

    public static TabGroup create(Long writerId, String name) {
        return new TabGroup(
                TabGroupId.EMPTY_TAB_GROUP_ID,
                UserId.create(writerId),
                TabGroupName.create(name),
                AuditTimestamps.now()
        );
    }

    public static TabGroup create(Long groupId, Long writerId, String name, LocalDateTime createAt, LocalDateTime updatedAt) {
        return new TabGroup(
                TabGroupId.create(groupId),
                UserId.create(writerId),
                TabGroupName.create(name),
                AuditTimestamps.create(createAt, updatedAt)
        );
    }

    private TabGroup(TabGroupId id, UserId writer, TabGroupName name, AuditTimestamps timestamps) {
        this.id = id;
        this.writer = writer;
        this.name = name;
        this.timestamps = timestamps;
    }

    public TabGroup updateAssignedId(Long id) {
        return new TabGroup(TabGroupId.create(id), this.writer, this.name, this.timestamps);
    }

    public TabGroup rename(String newName) {
        return new TabGroup(this.id, this.writer, TabGroupName.create(newName), this.timestamps);
    }

    public boolean isWriter(Long writerId) {
        return writer.isEqualId(writerId);
    }

    public boolean isNotWriter(Long writerId) {
        return !isWriter(writerId);
    }

    public Long getId() {
        return id.getValue();
    }

    public Long getWriterId() {
        return writer.getValue();
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
