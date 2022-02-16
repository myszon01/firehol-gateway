package com.myszon.service;

import com.myszon.model.Alias;
import com.myszon.processor.IpAddressProcessor;
import jakarta.inject.Singleton;

@Singleton
public class IngestService {
    private final IpAddressProcessor ipAddressIndexProcessor;

    public IngestService(IpAddressProcessor ipAddressIndexProcessor) {
        this.ipAddressIndexProcessor = ipAddressIndexProcessor;
    }

    public int startIngestion(Alias alias) throws Exception {
        if (Alias.IP_ADDRESS == alias) return this.ipAddressIndexProcessor.refreshIndex();

        throw new IllegalArgumentException("Alias doesn't exists");
    }

}
