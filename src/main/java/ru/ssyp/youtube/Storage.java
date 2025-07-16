package ru.ssyp.youtube;

import java.io.File;
import java.io.InputStream;

public interface Storage {
    void upload(String name, InputStream inputStream);

    InputStream download(String name);
}
