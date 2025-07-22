package ru.ssyp.youtube;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntCodecTest {

    @ParameterizedTest
    @ValueSource(ints = {1040239874, 42, 1024, 60555})
    void intToBytesTest(int number) {
        byte[] bytes = IntCodec.intToByte(number);
        System.out.println(bytes[0]);
        assertEquals(number, IntCodec.byteToInt(bytes));
    }
}
