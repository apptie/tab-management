package com.management.tab.application;

import com.management.tab.domain.group.TabGroup;
import com.management.tab.domain.repository.TabGroupRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TabGroupService {

    private final TabGroupRepository tabGroupRepository;

    public List<TabGroup> getAllGroups() {
        return tabGroupRepository.findAll();
    }

    public TabGroup getGroup(Long id) {
        return tabGroupRepository.findById(id);
    }

    @Transactional
    public Long createGroup(String name) {
        TabGroup tabGroup = TabGroup.create(name);

        return tabGroupRepository.save(tabGroup)
                                 .getId();
    }

    @Transactional
    public void updateGroup(Long id, String name) {
        TabGroup tabGroup = tabGroupRepository.findById(id);
        TabGroup renamedTabGroup = tabGroup.rename(name);

        tabGroupRepository.updateRenamed(renamedTabGroup);
    }

    @Transactional
    public void delete(Long id) {
        tabGroupRepository.delete(id);
    }

    public int countTabs(Long id) {
        return tabGroupRepository.countTabs(id);
    }
}
