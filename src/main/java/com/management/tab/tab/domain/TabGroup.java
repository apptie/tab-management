package com.management.tab.tab.domain;

import com.management.tab.tab.domain.dto.HierarchyDto;
import com.management.tab.tab.domain.exception.AbsentTabElementException;
import com.management.tab.tab.domain.exception.InvalidHierarchySizeException;
import com.management.tab.tab.domain.exception.InvalidTabElementHierarchyException;
import com.management.tab.tab.domain.exception.UnInitializedTabElementException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "id")
public class TabGroup {

    private static final int VALIDATE_HIERARCHY_START_INDEX = 1;
    private static final int VALID_DEPTH_VALUE = 1;
    private static final int VALID_ORDER_VALUE = 1;

    private Long id;
    private final Map<Long, TabElement> tabElements = new HashMap<>();

    public void addTabElement(TabElement tabElement) {
        if (tabElement.getId() == null) {
            throw new UnInitializedTabElementException();
        }

        tabElements.put(tabElement.getId(), tabElement);
    }

    public void changeTabElementContent(Long id, String title, String url, String description, boolean isPublic) {
        TabElement tabElement = tabElements.get(id);

        validateTabElement(tabElement);
        tabElement.changeTabElementContent(title, url, description, isPublic);
    }

    private void validateTabElement(TabElement tabElement) {
        if (tabElement == null) {
            throw new AbsentTabElementException();
        }
    }

    public void changeTabElementHierarchy(List<HierarchyDto> hierarchyDtos) {
        if (hierarchyDtos.size() != tabElements.size()) {
            throw new InvalidHierarchySizeException();
        }

        for (int i = VALIDATE_HIERARCHY_START_INDEX; i < hierarchyDtos.size(); i++) {
            validateHierarchy(hierarchyDtos.get(i - 1), hierarchyDtos.get(i));
        }

        for (HierarchyDto hierarchyDto : hierarchyDtos) {
            TabElement tabElement = tabElements.get(hierarchyDto.id());

            validateTabElement(tabElement);
            tabElement.changeTabElementHierarchy(hierarchyDto.order(), hierarchyDto.depth());
        }
    }

    private void validateHierarchy(HierarchyDto left, HierarchyDto right) {
        if (right.depth() - left.depth() > VALID_DEPTH_VALUE) {
            throw new InvalidTabElementHierarchyException();
        }

        if (right.order() - left.order() != VALID_ORDER_VALUE) {
            throw new InvalidTabElementHierarchyException();
        }
    }

    public void changeOgTag(Long id, String imageUrl, String title, String description) {
        TabElement tabElement = tabElements.get(id);

        validateTabElement(tabElement);
        tabElement.changeOgTag(imageUrl, title, description);
    }

    public void deleteTabElement(Long id) {
        TabElement removeTabElement = tabElements.remove(id);

        validateTabElement(removeTabElement);
    }
}
