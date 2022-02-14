package com.myszon.repository;

import com.myszon.model.IpAddress;
import com.myszon.model.SearchResults;

import java.io.IOException;

public interface IIndexSearch {

    SearchResults<IpAddress> findIpAddressById(String ipAddress) throws IOException;
}
