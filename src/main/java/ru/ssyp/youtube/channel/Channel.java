package ru.ssyp.youtube.channel;

import java.sql.SQLException;

public interface Channel {

    ChannelInfo channelInfo();

    boolean checkSubscription(int userId) throws SQLException, InvalidUserIdException;
    void subscribe(int userId) throws SQLException, InvalidUserIdException, AlreadySubscribedException;

    void unsubscribe(int userId) throws SQLException, NotSubscribedException, InvalidUserIdException;
}
