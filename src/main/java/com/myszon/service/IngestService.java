package com.myszon.service;

import com.myszon.model.Alias;
import com.myszon.model.Index;
import com.myszon.model.IpAddress;
import com.myszon.repository.IIndexIngest;
import com.myszon.repository.IIndexManager;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class IngestService {

    private final IIndexIngest indexIngest;
    private final IIndexManager indexManager;

    public IngestService(IIndexIngest indexIngest, IIndexManager indexManager) {
        this.indexIngest = indexIngest;
        this.indexManager = indexManager;
    }

    public boolean startIngestion(final Index index) {
        List<IpAddress> list = new ArrayList<>();

        for (int i = 0; i < 6; i ++) {
            IpAddress ip1 = new IpAddress();
            ip1.setIpAddress("1223232313_" + i);
            list.add(ip1);
        }


        try {
            indexManager.createIndex(Index.IP_ADDRESS_V1);
            indexManager.createAliasForIndex(Alias.IP_ADDRESS, Index.IP_ADDRESS_V1);
            indexIngest.insertIPAddressesToIndex(list, Index.IP_ADDRESS_V1);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

}
