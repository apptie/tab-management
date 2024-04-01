package com.management.tab.tag.domain.embed;

import lombok.Getter;

@Getter
public class TagCounter {

    public static final TagCounter DEFAULT = new TagCounter(
            0,
            0,
            0,
            0
    );

    private final long totalCount;
    private final long publicCount;
    private final long unitTotalCount;
    private final long unitPublicCount;

    private TagCounter(long totalCount, long publicCount, long unitTotalCount, long unitPublicCount) {
        this.totalCount = totalCount;
        this.publicCount = publicCount;
        this.unitTotalCount = unitTotalCount;
        this.unitPublicCount = unitPublicCount;
    }

    public TagCounter addCount(long unitTotalCount, long unitPublicCount) {
        return new TagCounter(
                this.totalCount + unitTotalCount,
                this.publicCount + unitPublicCount,
                unitTotalCount,
                unitPublicCount
        );
    }

    public long getPrivateCount() {
        return totalCount - publicCount;
    }

    public long getUnitPrivateCount() {
        return unitTotalCount - unitPublicCount;
    }
}
