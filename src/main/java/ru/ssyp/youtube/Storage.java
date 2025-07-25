package ru.ssyp.youtube;

import java.io.IOException;
import java.io.InputStream;

public interface Storage {
    void upload(String name, InputStream inputStream) throws IOException;

    void remove(String name) throws IOException;

    InputStream download(String name);
}


