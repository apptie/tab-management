package com.management.tab.domain.repository;

import com.management.tab.domain.group.TabGroup;
import java.util.List;

public interface TabGroupRepository {

    List<TabGroup> findAll();

    TabGroup findById(Long id);

    TabGroup save(TabGroup tabGroup);

    void updateRenamed(TabGroup renamedTabGroup);

    void delete(Long id);

    int countTabs(Long id);
}
