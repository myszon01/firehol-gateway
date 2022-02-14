package com.myszon.api.responses;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Commit {
    private String sha;
    private CommitHeader commit;

    public Commit() {}

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public CommitHeader getCommit() {
        return commit;
    }

    public void setCommit(CommitHeader commit) {
        this.commit = commit;
    }

    public static class CommitHeader {
        private Tree tree;

        public Tree getTree() {
            return tree;
        }

        public void setTree(Tree tree) {
            this.tree = tree;
        }
    }
}
