package com.myszon.model;

import com.myszon.service.SearchService;

import java.util.List;

public class SearchResults<TDocument> {

    List<TDocument> documents;

    protected SearchResults(SearchResults.Builder<TDocument> builder){
        this.documents = builder.documents;
    }

    public List<TDocument> getDocuments() {
        return documents;
    }


    public static class Builder<TDocument> {

        private List<TDocument> documents;

        protected Builder<TDocument> self() {
            return this;
        }

        public Builder<TDocument> documents(List<TDocument> documents) {
            this.documents = documents;
            return this.self();
        }

        public SearchResults<TDocument> build() {
            return new SearchResults<TDocument>(this);
        }
    }


}
