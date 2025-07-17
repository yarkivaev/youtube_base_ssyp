package ru.ssyp.youtube;

import java.io.InputStream;

public interface Youtube {
    void upload(Session user, String name, InputStream stream);

    InputStream load(Session user, String name);
}