package com.myszon.util;

import com.myszon.api.responses.Tree;
import com.myszon.api.responses.TreeType;

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


    public static long ipToLong(String ipAddress) {

        String[] ipAddressInArray = ipAddress.split("\\.");

        long result = 0;
        for (int i = 0; i < ipAddressInArray.length; i++) {

            int power = 3 - i;
            // add try catch
            int ip = Integer.parseInt(ipAddressInArray[i]);
            result += ip * Math.pow(256, power);

        }

        return result;
    }


    public static String longToIP(long longIP) {

        StringBuilder sb = new StringBuilder();
        for (int i = 3; i >= 0; i--) {

            sb.append((longIP >> (i*8)) & 0x000000FF);
            if(i != 0) sb.append(".");

        }

        return sb.toString();
    }

    public static boolean isValidIPAddress(String ipAddress) {
        if (ipAddress == null) return false;

        String[] segments = ipAddress.split("\\.");

        if (segments.length != 4) return false;

        for (String segment : segments) {
            try {
                int seg = Integer.parseInt(segment);
                if (seg > 255 || seg < 0) return false;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        return true;
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
}
