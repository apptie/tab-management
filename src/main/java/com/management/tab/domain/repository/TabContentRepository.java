package com.management.tab.domain.repository;

import com.management.tab.domain.content.TabContent;
import com.management.tab.domain.tab.vo.TabId;
import java.util.List;

public interface TabContentRepository {

    List<TabContent> findAllByTabId(TabId tabid);

    TabContent findById(Long id);

    TabContent save(TabContent tabContent);

    void update(TabContent updatedTabContent);

    void delete(Long id);

    void deleteAllByTabId(TabId tabId);

    int countByTabId(TabId tabId);

    class TabContentNotFoundException extends IllegalArgumentException {

        public TabContentNotFoundException() {
            super("탭 내용을 찾을 수 없습니다.");
        }
    }
}
