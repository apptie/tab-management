package com.management.tab.domain.tab;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import com.management.tab.domain.tab.vo.TabTitle;
import com.management.tab.domain.tab.vo.TabUrl;
import com.management.tab.domain.user.vo.UserId;
import java.util.Objects;

public class TabBuilder {

    private TabId id = TabId.EMPTY_TAB_ID;
    private TabId parentId = TabId.EMPTY_TAB_ID;
    private UserId writerId;
    private TabGroupId tabGroupId;
    private TabTitle title;
    private TabUrl url;
    private TabPosition position;
    private AuditTimestamps timestamps;

    public static TabBuilder builder() {
        return new TabBuilder();
    }

    public static TabBuilder createRoot(Long groupId, Long writerId, String title, String url, TabPosition position) {
        TabBuilder builder = new TabBuilder();

        builder.position = position;

        return builder.groupId(groupId)
                      .writerId(writerId)
                      .title(title)
                      .url(url);
    }

    public static TabBuilder createChild(Tab parentTab, String title, String url, TabPosition position) {
        TabBuilder builder = new TabBuilder();

        builder.tabGroupId = parentTab.tabGroupId();
        builder.parentId = parentTab.id();
        builder.writerId = parentTab.writerId();
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

    public TabBuilder writerId(Long writerId) {
        this.writerId = UserId.create(writerId);

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

        return new Tab(
                this.id,
                this.parentId,
                this.writerId,
                this.tabGroupId,
                this.title,
                this.url,
                getTabPosition(),
                getAuditTimestamps()
        );
    }

    private void validateValues() {
        Objects.requireNonNull(tabGroupId, "그룹 ID는 필수입니다.");
        Objects.requireNonNull(title, "제목은 필수입니다.");
        Objects.requireNonNull(url, "Url은 필수입니다.");
        Objects.requireNonNull(writerId, "작성자 ID는 필수입니다.");
    }

    private TabPosition getTabPosition() {
        if (position != null) {
            return position;
        }

        return TabPosition.defaultPosition();
    }

    private AuditTimestamps getAuditTimestamps() {
        if (timestamps != null) {
            return timestamps;
        }

        return AuditTimestamps.now();
    }
}
