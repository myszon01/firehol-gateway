package com.myszon.service;

import com.myszon.controller.projection.IngestResponse;
import com.myszon.model.Alias;
import com.myszon.processor.IndexProcessor;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class IngestService {
    private final List<IndexProcessor> indexManagers;

    public IngestService(List<IndexProcessor> indexManagers) {
        this.indexManagers = indexManagers;
    }

    public IngestResponse startIngestion(Alias alias) throws Exception {

        for(IndexProcessor indexManager : indexManagers) {
            if (indexManager.getAliases().contains(alias)) {
                return indexManager.refreshIndex();
            }
        }

        throw new IllegalArgumentException("Alias doesn't exists");
    }
}
