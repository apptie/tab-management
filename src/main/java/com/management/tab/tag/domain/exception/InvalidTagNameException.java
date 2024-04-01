package com.management.tab.tag.domain.exception;

public class InvalidTagNameException extends IllegalArgumentException {

    private static final String MESSAGE = "유효한 태그 이름이 아닙니다.";

    public InvalidTagNameException() {
        super(MESSAGE);
    }
}
