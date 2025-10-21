package com.management.tab.domain.tab.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TabPosition {

    private final Integer value;

    public static TabPosition defaultPosition() {
        return new TabPosition(0);
    }

    public TabPosition next() {
        return new TabPosition(value + 1);
    }

    public static TabPosition create(int value) {
        validateValue(value);

        return new TabPosition(value);
    }

    private static void validateValue(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("위치는 0 이상이어야 합니다");
        }
    }

    private TabPosition(Integer value) {
        this.value = value;
    }

    public boolean isFirst() {
        return value == 0;
    }
}
