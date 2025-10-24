package com.management.tab.domain.content.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TabContentId {

    public static final TabContentId EMPTY_TAB_CONTENT_ID = new TabContentId(null);

    private final Long value;

    public static TabContentId create(Long value) {
        validateValue(value);

        return new TabContentId(value);
    }

    private static void validateValue(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("TabContentId는 null일 수 없습니다.");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("TabContentId는 양수여야 합니다.");
        }
    }

    private TabContentId(Long value) {
        this.value = value;
    }
}
