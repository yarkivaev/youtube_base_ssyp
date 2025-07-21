package ru.ssyp.youtube;

import java.io.IOException;
import java.io.InputStream;

public interface ProtocolValue {

    InputStream rawContent() throws IOException;
}
