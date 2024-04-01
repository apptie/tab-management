package com.management.tab.tab.domain.exception;

public class AbsentTagException extends IllegalStateException {

    private static final String MESSAGE = "등록되지 않은 태그입니다.";

    public AbsentTagException() {
        super(MESSAGE);
    }
}
