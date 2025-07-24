package ru.ssyp.youtube.server;

import org.apache.commons.io.input.NullInputStream;
import ru.ssyp.youtube.Youtube;
import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.VideoMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class UploadVideoCommand implements Command {

    private final Session session;

    private final VideoMetadata metadata;

    private final Long fileSize;

    private final InputStream file;

    private final Youtube youtube;

    public UploadVideoCommand(Session session, VideoMetadata metadata, Long fileSize, InputStream file, Youtube youtube) {
        this.session = session;
        this.metadata = metadata;
        this.fileSize = fileSize;
        this.file = file;
        this.youtube = youtube;
    }


    @Override
    public InputStream act() throws SQLException {
        try {
            youtube.upload(this.session, this.metadata, this.file);
            return new NullInputStream();
        } catch (IOException | InterruptedException | InvalidChannelIdException e) {
            throw new RuntimeException(e);
        }
    }
}
