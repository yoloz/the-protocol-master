package org.kendar.buffers;

public class BBufferUtils {

    public static int getBit(byte[] data, int pos) {
        int posByte = pos / 8;
        int posBit = pos % 8;
        byte valByte = data[posByte];
        int valInt = (valByte >> posBit) & 0x0001;
        //valByte >> (8 - (posBit + 1)) & 0x0001;
        return valInt;
    }

    public static void setBit(byte[] data, int pos) {
        int posByte = pos / 8;
        int posBit = pos % 8;
        var newVal = (byte) (1 << posBit);
        data[posByte] = (byte) (newVal | data[posByte]);
    }

    public static byte toByte(int by) {
        var number = by & 0xff;
        return (byte) number;
    }

    public static byte[] toByteArray(int... data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            var val = data[i];
            result[i] = toByte(val);
        }
        return result;
    }

    public static void unSetBit(byte[] data, int pos) {
        int posByte = pos / 8;
        int posBit = pos % 8;
        var newVal = (byte) (1 << posBit);
        data[posByte] = (byte) (newVal ^ data[posByte]);
    }
}
