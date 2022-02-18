package com.myszon.api.responses;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class CommitHeader {
    private Tree tree;

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }
}
