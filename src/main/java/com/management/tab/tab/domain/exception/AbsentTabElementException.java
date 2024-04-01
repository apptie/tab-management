package com.management.tab.tab.domain.exception;

public class AbsentTabElementException extends IllegalArgumentException {

    private static final String MESSAGE = "지정한 탭 요소를 찾을 수 없습니다.";

    public AbsentTabElementException() {
        super(MESSAGE);
    }
}
