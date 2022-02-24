package com.myszon.api.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.core.annotation.Introspected;

import java.util.List;
import java.util.Objects;

@Introspected
public class Tree {

    private String sha;
    private TreeType type;
    private List<Tree> tree;
    private String path;

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public TreeType getType() {
        return type;
    }

    public void setType(String type) {
        this.type = TreeType.fromString(type);
    }

    public List<Tree> getTree() {
        return tree;
    }

    public void setTree(List<Tree> tree) {
        this.tree = tree;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tree tree = (Tree) o;

        return Objects.equals(sha, tree.sha);
    }

    @Override
    public int hashCode() {
        return sha != null ? sha.hashCode() : 0;
    }
}
