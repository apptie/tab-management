package com.management.tab.config.stub;

import com.management.tab.tab.domain.TabElement;
import com.management.tab.tab.domain.embed.OgTag;
import com.management.tab.tab.domain.embed.TabElementContent;
import com.management.tab.tab.domain.embed.TabElementHierarchy;

public class StubTabElement extends TabElement {

    private final Long id;
    private final TabElement tabElement;

    public StubTabElement(Long id, TabElement tabElement) {
        this.id = id;
        this.tabElement = tabElement;
    }

    public static StubTabElement of(Long id, String title, String url, String description, boolean isPublic, int order, int depth) {
        TabElement tabElement = TabElement.of(title, url, description, isPublic, order, depth);

        return new StubTabElement(id, tabElement);
    }

    @Override
    public void changeTabElementContent(String title, String url, String description, boolean isPublic) {
        tabElement.changeTabElementContent(title, url, description, isPublic);
    }

    @Override
    public void changeOgTag(String imageUrl, String title, String description) {
        tabElement.changeOgTag(imageUrl, title, description);
    }

    @Override
    public void changeTabElementHierarchy(int order, int depth) {
        tabElement.changeTabElementHierarchy(order, depth);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public TabElementContent getTabElementContent() {
        return tabElement.getTabElementContent();
    }

    @Override
    public TabElementHierarchy getTabElementHierarchy() {
        return tabElement.getTabElementHierarchy();
    }

    @Override
    public OgTag getOgTag() {
        return tabElement.getOgTag();
    }
}
