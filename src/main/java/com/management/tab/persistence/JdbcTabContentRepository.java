package com.management.tab.persistence;

import com.management.tab.domain.content.TabContent;
import com.management.tab.domain.content.vo.TabContentId;
import com.management.tab.domain.repository.TabContentRepository;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.persistence.dao.TabContentDao;
import com.management.tab.persistence.dao.dto.TabContentDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcTabContentRepository implements TabContentRepository {

    private final TabContentDao tabContentDao;

    @Override
    public List<TabContent> findAllByTabId(TabId tabid) {
        return tabContentDao.findAllByTabId(tabid.id())
                            .stream()
                            .map(TabContentDto::toTabContent)
                            .toList();
    }

    @Override
    public TabContent findById(Long id) {
        return tabContentDao.findById(id)
                            .map(TabContentDto::toTabContent)
                            .orElseThrow(() -> new IllegalArgumentException("지정한 탭 내용을 찾을 수 없습니다."));
    }

    @Override
    public TabContent save(TabContent tabContent) {
        Long tabContentId = tabContentDao.save(
                tabContent.getTabId().id(),
                tabContent.getContent().getValue(),
                tabContent.getAuditTimestamps().getCreatedAt(),
                tabContent.getAuditTimestamps().getUpdatedAt()
        );

        return tabContent.withId(TabContentId.create(tabContentId));
    }

    @Override
    public void update(TabContent updatedTabContent) {
        tabContentDao.update(
                updatedTabContent.getId().getValue(),
                updatedTabContent.getContent().getValue(),
                LocalDateTime.now()
        );
    }

    @Override
    public void delete(Long id) {
        tabContentDao.delete(id);
    }

    @Override
    public void deleteAllByTabId(TabId tabId) {
        tabContentDao.deleteAllByTabId(tabId.id());
    }

    @Override
    public int countByTabId(TabId tabId) {
        return tabContentDao.countByTabId(tabId.id());
    }
}
