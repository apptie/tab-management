package com.management.tab.domain.tab.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
public class TabUrl {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^https?://(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b[-a-zA-Z0-9()@:%_\\+.~#?&/=]*$"
    );

    private final String value;

    public static TabUrl create(String value) {
        validateValue(value);
        return new TabUrl(value);
    }

    private static void validateValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("탭 URL은 비어있을 수 없습니다");
        }

        if (!URL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("유효한 URL 형식이 아닙니다");
        }
    }

    private TabUrl(String value) {
        this.value = value;
    }
}
