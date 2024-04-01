package com.management.tab.tab.domain.exception;

public class UnInitializedTabElementException extends IllegalStateException {

    private static final String MESSAGE = "탭 요소가 생성되지 않았습니다.";

    public UnInitializedTabElementException() {
        super(MESSAGE);
    }
}
