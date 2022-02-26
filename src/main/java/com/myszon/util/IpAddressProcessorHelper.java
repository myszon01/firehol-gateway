package com.myszon.util;

import com.myszon.api.responses.Tree;
import com.myszon.api.responses.TreeType;
import io.micronaut.http.client.exceptions.ReadTimeoutException;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.ssl.SslHandshakeTimeoutException;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.HashSet;
import java.util.Set;

public class IpAddressProcessorHelper {

    private final static Set<String> FILE_EXTS;

    static {
        FILE_EXTS = new HashSet<>();
        FILE_EXTS.add("netset");
        FILE_EXTS.add("ipset");
    }

    public static boolean shouldIgnoreBlobOrTree(Tree tree) {
        if (tree.getType() == TreeType.TREE) return true;
        int extIdx = tree.getPath().lastIndexOf('.');
        String ext = tree.getPath().substring(extIdx + 1);
        return FILE_EXTS.contains(ext);
    }

    public static String[] getIpAddressAndOrMask(String ipAddress) {
        String[] segments = ipAddress.split("\\.");
        if (segments.length < 4) return new String[]{null, null};

        String[] lastSegmentAndOrMask = segments[3].split("/");
        String lastSegment = lastSegmentAndOrMask[0];
        String mask = lastSegmentAndOrMask.length > 1 ? lastSegmentAndOrMask[1] : null;

        return new String[]{String.format("%s.%s.%s.%s", segments[0],
                segments[1], segments[2], lastSegment), mask};
    }

    public static boolean isValidMask(String mask) {
        if (mask == null) return false;
        try {
            int m = Integer.parseInt(mask);
            return m <= 32 && m > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static boolean shouldIgnoreError(Throwable ex) {
        return ex.getCause() instanceof ConnectTimeoutException
                || ex.getCause() instanceof SslHandshakeTimeoutException
                || ex.getCause() instanceof IOException
                || ex.getCause() instanceof ClosedChannelException
                || ex.getCause() instanceof ReadTimeoutException
                || ex instanceof ReadTimeoutException;
    }
}
