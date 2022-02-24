package com.myszon.processor;

import com.myszon.controller.projection.IngestResponse;
import com.myszon.model.Alias;

import java.util.Set;

public interface IndexProcessor {

    Set<Alias> getAliases();

    IngestResponse refreshIndex() throws Exception;
}
