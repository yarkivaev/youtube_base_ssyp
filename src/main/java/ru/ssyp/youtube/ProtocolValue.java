package ru.ssyp.youtube;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public interface ProtocolValue {

    InputStream rawContent() throws IOException, SQLException;
}
