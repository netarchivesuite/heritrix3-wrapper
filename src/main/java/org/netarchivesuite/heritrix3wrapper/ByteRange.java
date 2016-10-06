package org.netarchivesuite.heritrix3wrapper;

public class ByteRange {

    public String type;

    public long from;

    public long to;

    public long contentLength;

    // bytes 100-126501/126502
    // bytes 1024-2047/128144
    public static ByteRange parse(String headerValue) {
        if (headerValue == null || headerValue.length() == 0) {
            return null;
        }
        try {
            int idx = 0;
            byte[] arr = headerValue.getBytes();
            int len = headerValue.length();
            while (idx < len && Character.isWhitespace(arr[idx] & 255)) {
                ++idx;
            }
            int pIdx = idx;
            while (idx < len && Character.isLetter(arr[idx] & 255)) {
                ++idx;
            }
            String type = headerValue.substring(pIdx, idx).toLowerCase();
            if (idx == len || arr[idx] != ' ') {
                return null;
            }
            while (idx < len && Character.isWhitespace(arr[idx] & 255)) {
                ++idx;
            }
            pIdx = idx;
            while (idx < len && arr[idx] != '-') {
                ++idx;
            }
            long from = Long.parseLong(headerValue.substring(pIdx, idx));
            if (idx == len || arr[idx] != '-') {
                return null;
            }
            pIdx = ++idx;
            while (idx < len && arr[idx] != '/') {
                ++idx;
            }
            long to = Long.parseLong(headerValue.substring(pIdx, idx));
            if (idx == len || arr[idx] != '/') {
                return null;
            }
            pIdx = ++idx;
            while (idx < len && Character.isDigit(arr[idx])) {
                ++idx;
            }
            long contentLength = Long.parseLong(headerValue.substring(pIdx, idx));
            ByteRange byteRange = new ByteRange();
            byteRange.type = type;
            byteRange.from = from;
            byteRange.to = to;
            byteRange.contentLength = contentLength;
            return byteRange;
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

}
