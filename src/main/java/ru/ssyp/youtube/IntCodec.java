package ru.ssyp.youtube;

public class IntCodec {

    public static byte[] intToByte(int integer){
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((integer >> 24) & 0xFF);
        bytes[1] = (byte) ((integer >> 16) & 0xFF);
        bytes[2] = (byte) ((integer >> 8) & 0xFF);
        bytes[3] = (byte) (integer & 0xFF);
        return bytes;
    }

    public static int byteToInt(byte[] bytes) {

        return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }
    public static long byteToInt_8(byte[] bytes) {

        return ((long) bytes[0] << 56) + ((long) bytes[1] << 48) + ((long) bytes[2] << 40) + ((long) bytes[3] << 32) + (bytes[4] << 24) + (bytes[5] << 16) + (bytes[6] << 8) + bytes[7];
    }
    public static int byteToInt_1(byte[] bytes) {

        return bytes[0];
    }
}

