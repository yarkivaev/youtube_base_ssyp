package ru.ssyp.youtube.server;

import java.io.InputStream;

public interface Command {
    InputStream act() throws RuntimeException;
}
