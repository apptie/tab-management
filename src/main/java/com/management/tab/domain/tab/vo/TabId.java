package com.management.tab.domain.tab.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TabId {

    private final Long value;

    public static TabId create(Long value) {
        validateValue(value);

        return new TabId(value);
    }

    private static void validateValue(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("TabId는 양수여야 합니다.");
        }
    }

    private TabId(Long value) {
        this.value = value;
    }

    public Long id() {
        return value;
    }
}

