package ru.ssyp.youtube.channel;

import java.sql.SQLException;

public interface ChannelInfo {
    String name() throws SQLException;

    String description() throws SQLException;

    int subscribers() throws SQLException;

    int owner() throws SQLException;

    int videoAmount();


}
