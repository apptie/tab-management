package com.management.tab.application;

import com.management.tab.domain.content.TabContent;
import com.management.tab.domain.content.vo.TabContentId;
import com.management.tab.domain.repository.TabContentRepository;
import com.management.tab.domain.tab.vo.TabId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TabContentService {

    private final TabContentRepository tabContentRepository;

    public List<TabContent> getAllContentsByTabId(TabId tabId) {
        return tabContentRepository.findAllByTabId(tabId);
    }

    public TabContent getContent(Long contentId) {
        return tabContentRepository.findById(contentId);
    }

    @Transactional
    public TabContentId createContent(TabId tabId, String content) {
        TabContent newTabContent = TabContent.create(tabId, content);

        return tabContentRepository.save(newTabContent)
                                   .getId();
    }

    @Transactional
    public void updateContent(Long contentId, String newContent) {
        TabContent tabContent = tabContentRepository.findById(contentId);
        TabContent updatedTabContent = tabContent.updateContent(newContent);

        tabContentRepository.update(updatedTabContent);
    }

    @Transactional
    public void delete(Long contentId) {
        tabContentRepository.delete(contentId);
    }

    @Transactional
    public void deleteAllByTabId(TabId tabId) {
        tabContentRepository.deleteAllByTabId(tabId);
    }

    public int countContents(TabId tabId) {
        return tabContentRepository.countByTabId(tabId);
    }
}
