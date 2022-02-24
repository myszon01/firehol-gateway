package com.myszon.repository;

import com.myszon.model.IpAddress;
import com.myszon.model.SearchResults;

import java.io.IOException;
import java.util.List;

public interface IIndexSearch {

    List<IpAddress> findIpAddressById(String ipAddress) throws IOException;
}
