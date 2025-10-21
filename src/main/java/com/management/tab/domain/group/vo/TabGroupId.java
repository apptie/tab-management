package com.management.tab.domain.group.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TabGroupId {

    private final Long value;

    public static TabGroupId create(Long value) {
        validateValue(value);

        return new TabGroupId(value);
    }

    private static void validateValue(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("GroupId는 양수여야 합니다");
        }
    }

    private TabGroupId(Long value) {
        this.value = value;
    }
}
