package ru.ssyp.youtube.server;

import ru.ssyp.youtube.channel.InvalidChannelDescriptionException;
import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.channel.InvalidChannelNameException;
import ru.ssyp.youtube.users.InvalidTokenException;
import ru.ssyp.youtube.video.InvalidVideoIdException;

import java.io.InputStream;
import java.sql.SQLException;

public interface Command {

    InputStream act() throws RuntimeException, SQLException, InvalidVideoIdException, InvalidChannelIdException;
}
