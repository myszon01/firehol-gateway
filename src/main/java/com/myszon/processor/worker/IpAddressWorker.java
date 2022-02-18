package com.myszon.processor.worker;

import com.myszon.api.responses.Blob;
import com.myszon.config.ElasticsearchProperties;
import com.myszon.model.Index;
import com.myszon.model.IpAddress;
import com.myszon.processor.manager.IpAddressManager;
import com.myszon.repository.IIndexIngest;
import com.myszon.util.IpAddressProcessorHelper;
import io.micronaut.core.async.subscriber.Completable;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static com.myszon.util.IpAddressProcessorHelper.isValidMask;

@Singleton
public class IpAddressWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpAddressManager.class);

    private final IIndexIngest indexIngest;
    private final int maxBulkSize;

    public IpAddressWorker(IIndexIngest indexIngest,
                           ElasticsearchProperties elasticsearchProperties) {
        this.indexIngest = indexIngest;
        this.maxBulkSize = elasticsearchProperties.getMaxBulkSize();
    }

    @Async
    @EventListener
    public void processBlob(BlobEvent ev) throws Exception {

        Integer docCounter = 0;
        Blob blob = ev.blob.block();
        Index ipAddress = ev.ipAddress;
        Index ipAddressRange = ev.ipAddressRange;
        String path = ev.path;

        assert blob != null;

        byte[] bytes = Base64.getMimeDecoder().decode(blob.getContent());
        String page = new String(bytes, StandardCharsets.UTF_8);
        String[] allLines = page.trim().split("\n");

        List<IpAddress> ipAddresses = new ArrayList<>();
        List<IpAddress> ipAddressesRange = new ArrayList<>();

        for (String line : allLines) {
            if (line.charAt(0) == '#') continue;

            String[] ipAddressAndMaskPair = IpAddressProcessorHelper.getIpAddressAndOrMask(line);
            String mask = ipAddressAndMaskPair[1];

            if (isValidMask(mask)) {
                ipAddressesRange.add(new IpAddress(line, blob.getSha(), path));
            } else {
                ipAddresses.add(new IpAddress(line, blob.getSha(), path));
            }

            if (ipAddresses.size() + 1 > this.maxBulkSize) {
                LOGGER.debug("MAX_BULK_SIZE reached in processIP function. Saving to db");
                this.indexIngest.insertIPAddressesToIndex(ipAddresses, ipAddress);
                docCounter += ipAddresses.size();
                ipAddresses = new ArrayList<>();
            }

            if (ipAddressesRange.size() + 1 > this.maxBulkSize) {
                LOGGER.debug("MAX_BULK_SIZE reached in processIP function. Saving to db");
                this.indexIngest.insertIPAddressesToIndex(ipAddresses, ipAddressRange);
                docCounter += ipAddressesRange.size();
                ipAddressesRange = new ArrayList<>();
            }

        }

        LOGGER.debug("Done executing processIP function. Saving to db");
        this.indexIngest.insertIPAddressesToIndex(ipAddressesRange, ipAddressRange);
        this.indexIngest.insertIPAddressesToIndex(ipAddresses, ipAddress);
        Integer total = docCounter + ipAddresses.size() + ipAddressesRange.size();
    }


    public static class BlobEvent {
        Mono<Blob> blob;
        String path;
        Index ipAddress;
        Index ipAddressRange;

        public BlobEvent(Mono<Blob> blob, Index ipAddress, Index ipAddressRange, String path) {
            this.blob = blob;
            this.ipAddress = ipAddress;
            this.ipAddressRange = ipAddressRange;
            this.path = path;
        }
    }

}
