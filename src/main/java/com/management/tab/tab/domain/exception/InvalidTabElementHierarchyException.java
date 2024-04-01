package com.management.tab.tab.domain.exception;

public class InvalidTabElementHierarchyException extends IllegalStateException {

    private static final String MESSAGE = "탭 요소 간의 계층 구조가 올바르지 않습니다.";

    public InvalidTabElementHierarchyException() {
        super(MESSAGE);
    }
}
