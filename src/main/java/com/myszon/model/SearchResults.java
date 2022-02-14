package com.myszon.model;

import com.myszon.service.SearchService;

import java.util.List;

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
            return new SearchResults<TDocument>(this);
        }
    }


}
