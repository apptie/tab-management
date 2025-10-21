package com.management.tab.domain.group;

import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.group.vo.TabGroupName;
import lombok.Getter;

@Getter
public class TabGroup {

    private final TabGroupId id;
    private final TabGroupName name;

    public static TabGroup create(String name) {
        return new TabGroup(null, TabGroupName.create(name));
    }

    public static TabGroup createWithAssignedId(Long id, TabGroup tabGroup) {
        return new TabGroup(TabGroupId.create(id), tabGroup.name);
    }

    public TabGroup rename(String newName) {
        return new TabGroup(this.id, TabGroupName.create(newName));
    }

    private TabGroup(TabGroupId id, TabGroupName name) {
        this.id = id;
        this.name = name;
    }
}
