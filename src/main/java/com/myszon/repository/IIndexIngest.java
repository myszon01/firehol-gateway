package com.myszon.repository;

import com.myszon.model.Index;
import com.myszon.model.IpAddress;

import java.io.IOException;
import java.util.List;

public interface IIndexIngest {

    boolean insertIPAddressesToIndex(List<IpAddress> ipAddress, Index index) throws IOException;
}
