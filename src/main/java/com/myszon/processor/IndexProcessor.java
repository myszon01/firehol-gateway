package com.myszon.processor;

import com.myszon.model.Alias;

public interface IndexProcessor {

    Alias getAlias();

    int refreshIndex() throws Exception;
}
