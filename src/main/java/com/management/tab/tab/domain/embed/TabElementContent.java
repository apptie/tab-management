package com.management.tab.tab.domain.embed;

import com.management.tab.common.domain.DomainBuilder;
import com.management.tab.tab.domain.exception.InvalidTabElementUrlException;
import lombok.Getter;

@Getter
public class TabElementContent {

    private static final String HTTPS_PREFIX = "https://";

    private final String title;
    private final String url;
    private final String description;
    private final boolean isPublic;

    public static TabElementContentBuilder builder() {
        return new TabElementContentBuilder();
    }

    private TabElementContent(String title, String url, String description, boolean isPublic) {
        validateUrl(url);

        this.title = title;
        this.url = url;
        this.description = description;
        this.isPublic = isPublic;
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

    public static class TabElementContentBuilder implements DomainBuilder<TabElementContent> {

        private String title;
        private String url;
        private String description;
        private boolean isPublic;

        public TabElementContentBuilder title(String title) {
            this.title = title;

            return this;
        }

        public TabElementContentBuilder url(String url) {
            this.url = url;

            return this;
        }

        public TabElementContentBuilder description(String description) {
            this.description = description;

            return this;
        }

        public TabElementContentBuilder isPublic(boolean isPublic) {
            this.isPublic = isPublic;

            return this;
        }

        @Override
        public TabElementContent build() {
            if (title == null || title.isBlank()) {
                this.title = this.url;
            }

            return new TabElementContent(title, url, description, isPublic);
        }
    }
}
