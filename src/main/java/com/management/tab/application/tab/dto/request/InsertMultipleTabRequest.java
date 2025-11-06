package com.management.tab.application.tab.dto.request;

import java.util.Collections;
import java.util.List;

public record InsertMultipleTabRequest(
        String title,
        String url,
        List<InsertMultipleTabRequest> children
) {
    public InsertMultipleTabRequest {
        if (children == null) {
            children = Collections.emptyList();
        }
    }
}
