package com.myszon.util;

import com.myszon.api.responses.Tree;
import jakarta.inject.Singleton;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// TEMP queue holder. Ideally that would be hosted in aws SNS -> SQS that would hold all not processed files.
@Singleton
public class QueueWrapper {
    private final BlockingQueue<Tree> sharedQueue = new LinkedBlockingQueue<>();

    public void put(Tree toPut) {
        try {
            sharedQueue.put(toPut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Tree take() {
        try {
            return sharedQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int size() {
        return sharedQueue.size();
    }
}