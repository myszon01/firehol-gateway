package com.myszon.repository;

import com.myszon.model.Alias;
import com.myszon.model.Index;
import org.opensearch.client.GetAliasesResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IIndexManager {

    boolean createIndex(Index name, Map<String, Object> mapping) throws IOException;

    boolean swapIndexAlias(Index fromIndex, Index toIndex, Alias alias) throws IOException;

    boolean createAliasForIndex(Alias alias, Index toIndex) throws IOException;

    boolean doesIndexExists(Index index) throws IOException;

    boolean doesAliasExists(Alias alias) throws IOException;

    boolean recreateIndex(Index index, Map<String, Object> mapping) throws IOException;

    List<Index> getIndexByAlias(Alias alias) throws IOException;
}
