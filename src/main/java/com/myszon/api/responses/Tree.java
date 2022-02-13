package com.myszon.api.responses;

import java.util.List;

public class Tree {

    private String sha;
    private TreeType type;
    private List<Tree> tree;

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public List<Tree> getUrl() {
        return tree;
    }

    public void setUrl(List<Tree> url) {
        this.tree = url;
    }

    public TreeType getType() {
        return type;
    }

    public void setType(TreeType type) {
        this.type = type;
    }

    public List<Tree> getTree() {
        return tree;
    }

    public void setTree(List<Tree> tree) {
        this.tree = tree;
    }
}
