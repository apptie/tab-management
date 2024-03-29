package com.management.tab.tab.domain.embed;

import lombok.Getter;

@Getter
public class OgTag {

    private static final String EMPTY = "";

    public static final OgTag DEFAULT_OG_TAG = new OgTag(EMPTY, EMPTY, EMPTY);

    private final String imageUrl;
    private final String title;
    private final String description;

    public OgTag(String imageUrl, String title, String description) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
    }

    public OgTag changeContent(String imageUrl, String title, String description) {
        return new OgTag(imageUrl, title, description);
    }
}
