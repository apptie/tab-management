package com.management.tab.domain.tab.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TabTitle {

    private static final int MAX_LENGTH = 50;

    private final String value;

    public static TabTitle create(String value) {
        validateValue(value);

        return new TabTitle(value);
    }

    private static void validateValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("탭 제목은 비어있을 수 없습니다.");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("탭 제목은 " + MAX_LENGTH + "자를 초과할 수 없습니다.");
        }
    }

    private TabTitle(String value) {
        this.value = value;
    }
}
