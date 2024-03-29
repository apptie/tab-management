package com.management.tab.tab.domain.embed;

import com.management.tab.tab.domain.exception.InvalidTabElementDepthException;
import com.management.tab.tab.domain.exception.InvalidTabElementOrderException;
import lombok.Getter;

@Getter
public class TabElementHierarchy {

    private final int order;
    private final int depth;

    public TabElementHierarchy(int order, int depth) {
        validateOrder(order);
        validateDepth(depth);

        this.order = order;
        this.depth = depth;
    }

    public TabElementHierarchy changeHierarchy(int order, int depth) {
        validateOrder(order);
        validateDepth(depth);

        return new TabElementHierarchy(order, depth);
    }

    private void validateOrder(int order) {
        if (order < 0) {
            throw new InvalidTabElementOrderException();
        }
    }

    private void validateDepth(int depth) {
        if (depth < 0) {
            throw new InvalidTabElementDepthException();
        }
    }
}
