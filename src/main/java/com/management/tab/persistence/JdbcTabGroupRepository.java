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
                .orElseThrow(() -> new IllegalArgumentException("지정한 ID에 해당하는 탭 그룹이 없습니다."));
    }

    @Override
    public TabGroup save(TabGroup tabGroup) {
        Long tabGroupId = tabGroupDao.save(
                tabGroup.getName().getValue(),
                tabGroup.getTimestamps().getCreatedAt(),
                tabGroup.getTimestamps().getUpdatedAt()
        );

        return TabGroup.createWithAssignedId(tabGroupId, tabGroup);
    }

    @Override
    public void updateRenamed(TabGroup renamedTabGroup) {
        tabGroupDao.update(renamedTabGroup.getId().getValue(), renamedTabGroup.getName().getValue());
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
