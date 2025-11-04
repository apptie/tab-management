package com.management.tab.persistence;

import com.management.tab.domain.group.TabGroup;
import com.management.tab.domain.repository.TabGroupRepository;
import com.management.tab.persistence.dao.TabGroupDao;
import com.management.tab.persistence.dao.dto.TabGroupDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcTabGroupRepository implements TabGroupRepository {

    private final TabGroupDao tabGroupDao;

    @Override
    public List<TabGroup> findAll() {
        return tabGroupDao.findAll()
                          .stream()
                          .map(TabGroupDto::toTabGroup)
                          .toList();
    }

    @Override
    public TabGroup findById(Long id) {
        return tabGroupDao.findById(id)
                .map(TabGroupDto::toTabGroup)
                .orElseThrow(TabGroupNotFoundException::new);
    }

    @Override
    public TabGroup save(TabGroup tabGroup) {
        Long tabGroupId = tabGroupDao.save(
                tabGroup.getName(),
                tabGroup.getCreatorId(),
                tabGroup.getCreatedAt(),
                tabGroup.getUpdatedAt()
        );

        return tabGroup.updateAssignedId(tabGroupId);
    }

    @Override
    public void updateRenamed(TabGroup renamedTabGroup) {
        tabGroupDao.update(renamedTabGroup.getId(), renamedTabGroup.getName());
    }

    @Override
    public void delete(Long id) {
        tabGroupDao.delete(id);
    }

    @Override
    public int countTabs(Long id) {
        return tabGroupDao.countTabs(id);
    }
}
