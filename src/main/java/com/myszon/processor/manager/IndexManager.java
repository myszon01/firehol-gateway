package com.myszon.processor.manager;

import com.myszon.model.Alias;

public interface IndexManager {

    Alias getAlias();

    int refreshIndex() throws Exception;
}
