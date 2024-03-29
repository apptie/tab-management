package com.management.tab.tab.domain.exception;

public class InvalidTabElementDepthException extends IllegalArgumentException {

    private static final String MESSAGE = "depth 값은 0 미만일 수 없습니다.";

    public InvalidTabElementDepthException() {
        super(MESSAGE);
    }
}
