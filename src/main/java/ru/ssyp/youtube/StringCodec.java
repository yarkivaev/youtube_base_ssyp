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

    public static byte[] stringToStream(String str) throws IOException {
        byte[] stringBytes = str.getBytes();
        int stringSize = stringBytes.length;
        byte[] stringSizeB = IntCodec.intToByte(stringSize);
        byte[] bytes = new byte[stringSize + 4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = stringSizeB[i];
        }
        for (int i = 4; i < stringSize + 4; i ++) {
            bytes[i] = stringBytes[i - 4];
        }
        return bytes;
    }
}
