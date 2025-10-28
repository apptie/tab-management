package com.management.tab.persistence.dao.dto;

import com.management.tab.domain.tab.Tab;
import com.management.tab.domain.tab.TabBuilder;
import java.time.LocalDateTime;

public record TabDto(
        Long id,
        Long groupId,
        Long parentId,
        Long writerId,
        String title,
        String url,
        int position,
        LocalDateTime createAt,
        LocalDateTime updatedAt
) {

    public Tab toTab() {
        TabBuilder builder = TabBuilder.builder()
                                       .groupId(this.groupId)
                                       .writerId(writerId)
                                       .title(this.title)
                                       .url(this.url)
                                       .position(this.position);

        if (this.id != null) {
            builder.id(this.id);
        }
        if (this.parentId != null) {
            builder.parentId(this.parentId);
        }

        return builder.build();
    }
}
