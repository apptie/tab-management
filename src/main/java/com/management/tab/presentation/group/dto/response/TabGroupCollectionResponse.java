package com.management.tab.presentation.group.dto.response;

import com.management.tab.domain.group.TabGroup;
import java.util.List;

public record TabGroupCollectionResponse(List<TabGroupResponse> groups) {

    public static TabGroupCollectionResponse from(List<TabGroup> tabGroups) {
        List<TabGroupResponse> responses = tabGroups.stream()
                                                    .map(TabGroupResponse::from)
                                                    .toList();

        return new TabGroupCollectionResponse(responses);
    }

    public record TabGroupResponse(
            Long id,
            String name,
            String createdAt,
            String updatedAt
    ) {

        public static TabGroupResponse from(TabGroup tabGroup) {
            return new TabGroupResponse(
                    tabGroup.getId().getValue(),
                    tabGroup.getName().getValue(),
                    tabGroup.getTimestamps().getCreatedAt().toString(),
                    tabGroup.getTimestamps().getUpdatedAt().toString()
            );
        }
    }
}

