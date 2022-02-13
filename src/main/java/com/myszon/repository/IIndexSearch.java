package com.myszon.repository;

import com.myszon.model.IpAddress;

public interface IIndexSearch {

    public IpAddress findByIpAddress(String ipAddress);
}
