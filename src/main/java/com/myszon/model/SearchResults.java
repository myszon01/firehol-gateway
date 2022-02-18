package com.myszon.model;

import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
public class SearchResults<TDocument> {

    private final List<TDocument> documents;
    private final boolean found;

    protected SearchResults(SearchResults.Builder<TDocument> builder){
        this.documents = builder.documents;
        this.found = builder.found;
    }

    public List<TDocument> getDocuments() {
        return documents;
    }

    public boolean isFound() {
        return found;
    }

    public static class Builder<TDocument> {

        private List<TDocument> documents;
        private boolean found;

        protected Builder<TDocument> self() {
            return this;
        }

        public Builder<TDocument> documents(List<TDocument> documents) {
            this.documents = documents;
            return this.self();
        }

        public Builder<TDocument> found(boolean found) {
            this.found = found;
            return this.self();
        }

        public SearchResults<TDocument> build() {
            found = documents.size() > 0;
            return new SearchResults<TDocument>(this);
        }
    }


}
