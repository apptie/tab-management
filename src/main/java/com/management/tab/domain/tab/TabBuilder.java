package com.management.tab.domain.tab;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import com.management.tab.domain.tab.vo.TabTitle;
import com.management.tab.domain.tab.vo.TabUrl;
import java.util.Objects;

public class TabBuilder {

    private TabId id;
    private TabId parentId;
    private TabGroupId tabGroupId;
    private TabTitle title;
    private TabUrl url;
    private TabPosition position;
    private AuditTimestamps timestamps;

    public static TabBuilder builder() {
        return new TabBuilder();
    }

    public static TabBuilder createRoot(Long groupId, String title, String url, TabPosition position) {
        TabBuilder builder = new TabBuilder();

        builder.position = position;

        return builder.groupId(groupId)
                      .title(title)
                      .url(url);
    }

    public static TabBuilder createChild(Tab parentTab, String title, String url, TabPosition position) {
        TabBuilder builder = new TabBuilder();

        builder.tabGroupId = parentTab.getTabGroupId();
        builder.parentId = parentTab.getId();
        builder.position = position;

        return builder.title(title)
                      .url(url);
    }

    private TabBuilder() {
    }

    public TabBuilder id(Long id) {
        this.id = TabId.create(id);

        return this;
    }

    public TabBuilder parentId(Long parentId) {
        this.parentId = TabId.create(parentId);

        return this;
    }

    public TabBuilder groupId(Long groupId) {
        this.tabGroupId = TabGroupId.create(groupId);

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
                tabGroupId,
                title,
                url,
                finalPosition,
                auditTimestamps
        );
    }

    private void validateValues() {
        Objects.requireNonNull(tabGroupId, "그룹 ID는 필수입니다.");
        Objects.requireNonNull(title, "제목은 필수입니다.");
        Objects.requireNonNull(url, "Url은 필수입니다.");
    }
}
