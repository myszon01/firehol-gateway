package com.myszon.service;

import com.myszon.model.Alias;
import com.myszon.processor.manager.IpAddressManager;
import jakarta.inject.Singleton;

@Singleton
public class IngestService {
    private final IpAddressManager ipAddressIndexProcessor;

    public IngestService(IpAddressManager ipAddressIndexProcessor) {
        this.ipAddressIndexProcessor = ipAddressIndexProcessor;
    }

    public int startIngestion(Alias alias) throws Exception {
        if (Alias.IP_ADDRESS == alias) return this.ipAddressIndexProcessor.refreshIndex();

        throw new IllegalArgumentException("Alias doesn't exists");
    }

}
