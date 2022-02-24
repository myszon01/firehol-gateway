package com.myszon.processor.queue;

import com.myszon.api.responses.Tree;
import com.myszon.model.Index;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DLQ {
    private static final Queue<DLQ_MESSAGE> failedDLQ = new ConcurrentLinkedDeque<>();

    public static void addTree(DLQ_MESSAGE msg) {
        failedDLQ.add(msg);
    }

    public static DLQ_MESSAGE poll() {
        return failedDLQ.poll();
    }


    public static int size() {
        return failedDLQ.size();
    }

    public static boolean isEmpty() {
        return failedDLQ.isEmpty();
    }


    public static class DLQ_MESSAGE {
        Tree tree;
        Index index;
        Index indexRange;

        public DLQ_MESSAGE(Tree tree, Index index, Index indexRange) {
            this.tree = tree;
            this.index = index;
            this.indexRange = indexRange;
        }

        public Tree getTree() {
            return tree;
        }

        public Index getIndex() {
            return index;
        }

        public Index getIndexRange() {
            return indexRange;
        }
    }
}
