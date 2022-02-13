package com.myszon.api.responses;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Commit {
    private String sha;
    private Tree tree;

    public Commit() {}

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }
}
