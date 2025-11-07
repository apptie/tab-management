package com.management.tab.application.tab.dto.response;

import java.util.List;

public record InsertMultipleTabResponse(
        Long tabId,
        String title,
        String url,
        int depth,
        int position,
        List<InsertMultipleTabResponse> children
) {
}
