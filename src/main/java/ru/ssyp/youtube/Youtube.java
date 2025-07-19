package ru.ssyp.youtube;

import java.io.File;
import java.io.InputStream;

public interface Youtube {
    void upload(User user, uploadSignature str, InputStream stream);

//    InputStream load(User user, String name);
}