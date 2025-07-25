package ru.ssyp.youtube.server;

import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.video.InvalidVideoIdException;

import java.io.IOException;
import java.io.InputStream;

public interface Command {
    InputStream act() throws RuntimeException, InvalidVideoIdException, InvalidChannelIdException, IOException;
}
