package com.management.tab.tag.domain.embed;

import com.management.tab.common.domain.DomainBuilder;
import com.management.tab.tag.domain.exception.InvalidTagNameException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "id")
public class Tag {

    private Long id;
    private String name;
    private TagCounter counter;

    public static TagBuilder builder() {
        return new TagBuilder();
    }

    private Tag(Long id, String name, TagCounter tagCounter) {
        this.id = id;
        this.name = name;
        this.counter = tagCounter;
    }

    public void addCount(long unitTotalCount, long unitPublicCount) {
        this.counter = counter.addCount(unitTotalCount, unitPublicCount);
    }

    public void changeName(String name) {
        this.name = name;
    }

    public long getPrivateCount() {
        return counter.getPrivateCount();
    }

    public long getUnitPrivateCount() {
        return counter.getUnitPrivateCount();
    }

    public long getTotalCount() {
        return counter.getTotalCount();
    }

    public long getPublicCount() {
        return counter.getPublicCount();
    }

    public long getUnitTotalCount() {
        return counter.getUnitTotalCount();
    }

    public long getUnitPublicCount() {
        return counter.getUnitPublicCount();
    }

    public static class TagBuilder implements DomainBuilder<Tag> {

        private Long id;
        private String name;
        private TagCounter tagCounter = TagCounter.DEFAULT;

        public TagBuilder id(Long id) {
            this.id = id;

            return this;
        }

        public TagBuilder name(String name) {
            this.name = name;

            return this;
        }

        public TagBuilder tagCounter(TagCounter tagCounter) {
            this.tagCounter = tagCounter;

            return this;
        }

        @Override
        public Tag build() {
            validateName(name);

            return new Tag(id, name, tagCounter);
        }

        private void validateName(String name) {
            if (isInvalidName(name)) {
                throw new InvalidTagNameException();
            }
        }

        private boolean isInvalidName(String name) {
            return name == null || name.isBlank();
        }
    }
}
