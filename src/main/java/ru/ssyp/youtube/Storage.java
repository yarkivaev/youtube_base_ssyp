package ru.ssyp.youtube;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface Storage {
    void upload(String name, InputStream inputStream);

    InputStream download(String name) throws IOException;
}
