package com.management.tab.tab.domain;

import com.management.tab.tab.domain.embed.OgTag;
import com.management.tab.tab.domain.embed.TabElementContent;
import com.management.tab.tab.domain.embed.TabElementHierarchy;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "id")
public class TabElement {

    private Long id;
    private TabElementContent tabElementContent;
    private TabElementHierarchy tabElementHierarchy;
    private OgTag ogTag;

    protected TabElement() {
    }

    private TabElement(
            TabElementContent tabElementContent,
            TabElementHierarchy tabElementHierarchy,
            OgTag ogTag
    ) {
        this.tabElementContent = tabElementContent;
        this.tabElementHierarchy = tabElementHierarchy;
        this.ogTag = ogTag;
    }

    public static TabElement of(String title, String url, String description, boolean isPublic, int order, int depth) {
        TabElementContent tabElementContent = TabElementContent.of(title, url, description, isPublic);
        TabElementHierarchy tabElementHierarchy = new TabElementHierarchy(order, depth);

        return new TabElement(tabElementContent, tabElementHierarchy, OgTag.DEFAULT);
    }

    public void changeTabElementContent(String title, String url, String description, boolean isPublic) {
        tabElementContent = tabElementContent.changeContent(title, url, description, isPublic);
    }

    public void changeOgTag(String imageUrl, String title, String description) {
        ogTag = ogTag.changeContent(imageUrl, title, description);
    }

    public void changeTabElementHierarchy(int order, int depth) {
        tabElementHierarchy = tabElementHierarchy.changeHierarchy(order, depth);
    }
}
