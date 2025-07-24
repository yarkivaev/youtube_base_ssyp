package ru.ssyp.youtube;

import java.io.IOException;
import java.io.InputStream;

public class StringCodec {
    public static String streamToString(InputStream stream) throws IOException {
        byte[] intBuffer = new byte[4];
        stream.read(intBuffer);
        int stringSize = IntCodec.byteToInt(intBuffer);
        byte[] stringBytes = new byte[stringSize];
        stream.read(stringBytes);
        return new String(stringBytes);
    }

    public static byte[] stringToStream(String str) {
        byte[] stringBytes = str.getBytes();
        int stringSize = stringBytes.length;
        byte[] stringSizeB = IntCodec.intToByte(stringSize);
        byte[] bytes = new byte[stringSize + 4];
        System.arraycopy(stringSizeB, 0, bytes, 0, 4);
        System.arraycopy(stringBytes, 0, bytes, 4, stringSize + 4 - 4);
        return bytes;
    }
}
