package com.management.tab.tab.domain.exception;

public class InvalidOgTagContentException extends IllegalStateException {

    private static final String MESSAGE = "OG Tag 값을 정상적으로 가져오지 못했습니다.";

    public InvalidOgTagContentException() {
        super(MESSAGE);
    }
}
