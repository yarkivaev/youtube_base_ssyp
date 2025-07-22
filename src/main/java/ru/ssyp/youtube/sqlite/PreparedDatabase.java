package ru.ssyp.youtube.sqlite;

import java.sql.Connection;

public interface PreparedDatabase {
    /**
     * Get the connection and lazily initialize the DB if needed.
     */
    Connection conn();
}
