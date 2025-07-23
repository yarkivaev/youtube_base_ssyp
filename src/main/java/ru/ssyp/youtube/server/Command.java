package ru.ssyp.youtube.server;

import ru.ssyp.youtube.channel.InvalidChannelDescriptionException;
import ru.ssyp.youtube.channel.InvalidChannelNameException;
import ru.ssyp.youtube.users.InvalidTokenException;

import java.io.InputStream;

public interface Command {

    InputStream act() throws RuntimeException;
}
