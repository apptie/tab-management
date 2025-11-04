package com.management.tab.domain.repository;

import com.management.tab.domain.group.TabGroup;
import java.util.List;

public interface TabGroupRepository {

    List<TabGroup> findAll();

    TabGroup findById(Long id);

    TabGroup save(TabGroup tabGroup);

    void updateRenamed(TabGroup renamedTabGroup);

    void delete(TabGroup tabGroup);

    int countTabs(Long id);

    class TabGroupNotFoundException extends IllegalArgumentException {

        public TabGroupNotFoundException() {
            super("탭 그룹을 찾을 수 없습니다.");
        }
    }
}
