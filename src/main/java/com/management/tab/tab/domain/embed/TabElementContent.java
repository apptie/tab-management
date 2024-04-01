package com.management.tab.tab.domain.embed;

import com.management.tab.tab.domain.exception.InvalidTabElementUrlException;
import lombok.Getter;

@Getter
public class TabElementContent {

    private static final String HTTPS_PREFIX = "https://";

    private final String title;
    private final String url;
    private final String description;
    private final boolean isPublic;

    private TabElementContent(String title, String url, String description, boolean isPublic) {
        validateUrl(url);

        this.title = title;
        this.url = url;
        this.description = description;
        this.isPublic = isPublic;
    }

    public static TabElementContent of(String title, String url, String description, boolean isPublic) {
        if (isInvalidTitle(title)) {
            return new TabElementContent(url, url, description, isPublic);
        }
        return new TabElementContent(title, url, description, isPublic);
    }

    private static boolean isInvalidTitle(String title) {
        return title == null || title.isBlank();
    }

    public TabElementContent changeContent(String title, String url, String description, boolean isPublic) {
        validateUrl(url);

        return new TabElementContent(title, url, description, isPublic);
    }

    private void validateUrl(String url) {
        if (isInvalidUrl(url)) {
            throw new InvalidTabElementUrlException();
        }
    }

    private boolean isInvalidUrl(String url) {
        return url == null || url.isBlank() || !url.startsWith(HTTPS_PREFIX);
    }
}
