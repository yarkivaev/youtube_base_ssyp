package ru.ssyp.youtube.server;

import ru.ssyp.youtube.Youtube;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.users.Users;

import java.io.InputStream;

public class DeleteVideoCommand implements Command{

    private final Session session;

    private final int videoId;

    private final Youtube youtube;

    public DeleteVideoCommand(Session session, int videoId, Youtube youtube) {
        this.session = session;
        this.videoId = videoId;
        this.youtube = youtube;
    }

    @Override
    public InputStream act() throws RuntimeException {
        return null;
    }
}
