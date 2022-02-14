package com.myszon.repository;

import com.myszon.model.Alias;
import com.myszon.model.Index;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface IIndexManager {

    boolean createIndex(Index name) throws IOException;

    String deleteIndex(Index name);

    boolean swapIndexAlias(Index fromIndex, Index toIndex, Alias alias) throws IOException;

    boolean createAliasForIndex(Alias alias, Index toIndex) throws IOException;

    boolean doesIndexExists(Index index) throws IOException;

    boolean doesAliasExists(Alias alias) throws IOException;

    Set<Index> getIndexByAlias(Alias alias) throws IOException;
}