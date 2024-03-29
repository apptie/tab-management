package com.management.tab.tab.domain.exception;

public class InvalidTabElementUrlException extends IllegalArgumentException {

    private static final String MESSAGE = "잘못된 URL 입니다.";

    public InvalidTabElementUrlException() {
        super(MESSAGE);
    }
}
