package com.management.tab.tab.domain.exception;

public class InvalidTabElementOrderException extends IllegalArgumentException {

    private static final String MESSAGE = "order 값은 0 미만일 수 없습니다.";

    public InvalidTabElementOrderException() {
        super(MESSAGE);
    }
}
