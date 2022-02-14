package com.myszon.service;

import com.myszon.model.IpAddress;
import com.myszon.model.SearchResults;
import com.myszon.repository.IIndexSearch;
import jakarta.inject.Singleton;

import java.io.IOException;

@Singleton
public class SearchService {

    private final IIndexSearch indexSearch;

    public SearchService(IIndexSearch indexSearch) {
        this.indexSearch = indexSearch;
    }

    public SearchResults<IpAddress> findIpAddressById(String id) throws IOException {
        return indexSearch.findIpAddressById(id);
    }
}
