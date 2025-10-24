package com.management.tab.presentation.group.dto.response;

import com.management.tab.domain.group.TabGroup;

public record TabGroupResponse(
        Long id,
        String name,
        String createdAt,
        String updatedAt
) {

    public static TabGroupResponse from(TabGroup tabGroup) {
        return new TabGroupResponse(
                tabGroup.getId(),
                tabGroup.getName(),
                tabGroup.getCreatedAt().toString(),
                tabGroup.getUpdatedAt().toString()
        );
    }
}
