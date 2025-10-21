package com.management.tab.domain.tab.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class GroupId {

    private final Long value;

    public static GroupId create(Long value) {
        validateValue(value);

        return new GroupId(value);
    }

    private static void validateValue(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("GroupId는 양수여야 합니다");
        }
    }

    private GroupId(Long value) {
        this.value = value;
    }
}
