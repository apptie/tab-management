package com.management.tab.tab.domain.exception;

public class InvalidHierarchySizeException extends IllegalArgumentException {

    private static final String MESSAGE = "탭 요소 개수와 탭 정렬 변경 사항이 일치하지 않습니다.";

    public InvalidHierarchySizeException() {
        super(MESSAGE);
    }
}
