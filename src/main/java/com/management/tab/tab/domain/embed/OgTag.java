package com.management.tab.tab.domain.embed;

import com.management.tab.tab.domain.exception.InvalidOgTagContentException;
import lombok.Getter;

@Getter
public class OgTag {

    private static final String EMPTY = "";

    public static final OgTag DEFAULT = new OgTag(EMPTY, EMPTY, EMPTY);

    private final String imageUrl;
    private final String title;
    private final String description;

    private OgTag(String imageUrl, String title, String description) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
    }

    public OgTag changeContent(String imageUrl, String title, String description) {
        validateContent(imageUrl, title, description);

        return new OgTag(imageUrl, title, description);
    }

    private void validateContent(String imageUrl, String title, String description) {
        if (isInvalidImageUrl(imageUrl) || isInvalidTitle(title) || isInvalidDescription(description)) {
            throw new InvalidOgTagContentException();
        }
    }

    private boolean isInvalidImageUrl(String imageUrl) {
        return imageUrl == null || imageUrl.isBlank();
    }

    private boolean isInvalidTitle(String title) {
        return title == null || title.isBlank();
    }

    private boolean isInvalidDescription(String description) {
        return description == null || description.isBlank();
    }
}
