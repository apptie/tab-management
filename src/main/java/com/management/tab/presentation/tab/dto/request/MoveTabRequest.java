package com.management.tab.presentation.tab.dto.request;

public record MoveTabRequest(Long newParentId, boolean withSubtree) {
}
