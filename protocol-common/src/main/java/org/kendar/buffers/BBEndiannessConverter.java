package org.kendar.buffers;

public class BBEndiannessConverter {
    public static byte[] swap8Bytes(byte[] bytes, int offset) {
        var result = swap4Bytes(bytes, 0);
        //noinspection DataFlowIssue
        result = swap4Bytes(bytes, 4);
        for (var i = 0; i < 4; i++) {
            var b = result[i];
            result[i] = result[i + 4];
            result[i + 4] = b;
        }
        return result;
    }

    public static byte[] swap4Bytes(byte[] bytes, int offset) {
        byte b = bytes[offset];
        bytes[offset] = bytes[offset + 3];
        bytes[offset + 3] = b;
        b = bytes[offset + 1];
        bytes[offset + 1] = bytes[offset + 2];
        bytes[offset + 2] = b;
        return bytes;
    }

    public static byte[] swap2Bytes(byte[] bytes, int offset) {
        byte b = bytes[offset];
        bytes[offset] = bytes[offset + 1];
        bytes[offset + 1] = b;
        return bytes;
    }
}
