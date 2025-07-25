package ru.ssyp.youtube.channel;

import ru.ssyp.youtube.users.Session;

import java.sql.SQLException;

public interface Channels {
    Channel channel(int channelId);

    Channel addNew(Session session, String name, String description) throws InvalidChannelNameException, InvalidChannelDescriptionException;

    void removeChannel(Session session, int channelId) throws InvalidChannelIdException, ForeignChannelIdException;

    Channel[] getUserChannel(int userId);
}
