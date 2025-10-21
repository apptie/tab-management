package com.management.tab.domain.tab;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.tab.vo.GroupId;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import com.management.tab.domain.tab.vo.TabTitle;
import com.management.tab.domain.tab.vo.TabUrl;
import java.util.Objects;

public class TabBuilder {

    private TabId id;
    private TabId parentId;
    private GroupId groupId;
    private TabTitle title;
    private TabUrl url;
    private TabPosition position;
    private AuditTimestamps timestamps;

    public static TabBuilder builder() {
        return new TabBuilder();
    }

    public static TabBuilder createRoot(Long groupId, String title, String url) {
        TabBuilder builder = new TabBuilder();

        builder.groupId = GroupId.create(groupId);
        builder.title = TabTitle.create(title);
        builder.url = TabUrl.create(url);

        return builder;
    }

    public static TabBuilder createChild(GroupId groupId, TabId parentId, String title, String url) {
        validateParentId(parentId);

        TabBuilder builder = new TabBuilder();

        builder.groupId = groupId;
        builder.parentId = parentId;

        return builder.title(title)
                      .url(url);
    }

    private static void validateParentId(TabId parentId) {
        Objects.requireNonNull(parentId, "부모 ID는 필수입니다.");
    }

    public static TabBuilder createWithAssignedId(Long tabId, Tab tab) {
        TabBuilder builder = new TabBuilder();

        builder.id = TabId.create(tabId);
        builder.parentId = tab.getParentId();
        builder.groupId = tab.getGroupId();
        builder.title = tab.getTitle();
        builder.url = tab.getUrl();
        builder.position = tab.getPosition();
        builder.timestamps = tab.getTimestamps();

        return builder;
    }

    private TabBuilder() {
    }

    public TabBuilder parentId(Long parentId) {
        this.parentId = TabId.create(parentId);

        return this;
    }

    public TabBuilder groupId(Long groupId) {
        this.groupId = GroupId.create(groupId);

        return this;
    }

    public TabBuilder title(String title) {
        this.title = TabTitle.create(title);

        return this;
    }

    public TabBuilder url(String url) {
        this.url = TabUrl.create(url);

        return this;
    }

    public TabBuilder position(int position) {
        this.position = TabPosition.create(position);

        return this;
    }

    public Tab build() {
        validateValues();

        TabPosition finalPosition = position != null ? position : TabPosition.defaultPosition();
        AuditTimestamps auditTimestamps = timestamps != null ? timestamps : AuditTimestamps.now();

        return new Tab(
                id,
                parentId,
                groupId,
                title,
                url,
                finalPosition,
                auditTimestamps
        );
    }

    private void validateValues() {
        Objects.requireNonNull(groupId, "그룹 ID는 필수입니다.");
        Objects.requireNonNull(title, "제목은 필수입니다.");
        Objects.requireNonNull(url, "Url은 필수입니다.");
    }
}
