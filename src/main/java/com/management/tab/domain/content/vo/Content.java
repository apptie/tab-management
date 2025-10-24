package com.management.tab.domain.content.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Content {

    private final String value;

    public static Content create(String value) {
        validateValue(value);

        return new Content(value);
    }

    private static void validateValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Content는 비어 있을 수 없습니다.");
        }
    }

    private Content(String value) {
        this.value = value;
    }
}
