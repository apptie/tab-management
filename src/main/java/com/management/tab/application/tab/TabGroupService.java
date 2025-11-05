package com.management.tab.application.tab;

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

    public List<TabGroup> getAllWriterGroups(Long writerId) {
        return tabGroupRepository.findAllByWriterId(writerId);
    }

    public TabGroup getGroup(Long id) {
        return tabGroupRepository.findById(id);
    }

    @Transactional
    public Long createGroup(Long creatorId, String name) {
        TabGroup tabGroup = TabGroup.create(creatorId, name);

        return tabGroupRepository.save(tabGroup)
                                 .getId();
    }

    @Transactional
    public void updateGroup(Long id, String name, Long updaterId) {
        TabGroup tabGroup = tabGroupRepository.findById(id);

        if (tabGroup.isNotWriter(updaterId)) {
            throw new TabGroupForbiddenException();
        }

        TabGroup renamedTabGroup = tabGroup.rename(name);

        tabGroupRepository.updateRenamed(renamedTabGroup);
    }

    @Transactional
    public void delete(Long id, Long deleterId) {
        TabGroup tabGroup = tabGroupRepository.findById(id);

        if (tabGroup.isNotWriter(deleterId)) {
            throw new TabGroupForbiddenException();
        }

        tabGroupRepository.delete(tabGroup);
    }

    public int countTabs(Long id) {
        return tabGroupRepository.countTabs(id);
    }

    public static class TabGroupForbiddenException extends IllegalArgumentException {

        public TabGroupForbiddenException() {
            super("탭 그룹 작성자가 아닙니다.");
        }
    }
}
