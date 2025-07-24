package ru.ssyp.youtube.channel;

import ru.ssyp.youtube.ProtocolValue;

import java.sql.SQLException;

public interface ChannelInfo extends ProtocolValue {
    int id();

    String name() throws SQLException;

    String description() throws SQLException;

    int subscribers() throws SQLException;

    int owner() throws SQLException;

    int videoAmount() throws SQLException;
}
