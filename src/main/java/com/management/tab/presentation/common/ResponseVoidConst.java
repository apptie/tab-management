package com.management.tab.presentation.common;

import org.springframework.http.ResponseEntity;

public final class ResponseVoidConst {

    public static final ResponseEntity<Void> NO_CONTENT = ResponseEntity.noContent().build();
    public static final ResponseEntity<Void> OK = ResponseEntity.ok().build();

    private ResponseVoidConst() {
    }
}
