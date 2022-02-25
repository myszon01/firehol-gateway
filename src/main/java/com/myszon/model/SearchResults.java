package com.myszon.model;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Introspected
@Builder
@Jacksonized
@Getter
public class SearchResults<TDocument> {

    private final List<TDocument> documents;
    private final boolean found;


}
