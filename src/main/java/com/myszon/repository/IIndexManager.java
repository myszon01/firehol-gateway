package com.myszon.repository;

import com.myszon.model.Alias;
import com.myszon.model.Index;

import java.io.IOException;

public interface IIndexManager {

    boolean createIndex(Index name) throws IOException;

    String deleteIndex(Index name);

    String swapIndexAlias(Index fromIndex, Index toIndex, Alias alias);

    boolean createAliasForIndex(Alias alias, Index toIndex) throws IOException;
}
