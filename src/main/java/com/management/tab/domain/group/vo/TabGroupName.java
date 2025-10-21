package com.management.tab.domain.group.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TabGroupName {

    private static final int MAX_LENGTH = 100;

    private final String value;

    public static TabGroupName create(String value) {
        validateValue(value);

        return new TabGroupName(value);
    }

    private static void validateValue(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("탭 그룹 이름은 비어있을 수 없습니다.");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("탭 그룹 이름은 " + MAX_LENGTH + "자를 초과할 수 없습니다.");
        }
    }

    private TabGroupName(String value) {
        this.value = value;
    }
}

