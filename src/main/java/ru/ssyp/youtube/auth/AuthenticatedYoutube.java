package ru.ssyp.youtube.auth;

import ru.ssyp.youtube.Youtube;
import ru.ssyp.youtube.users.Session;

import java.io.IOException;
import java.io.InputStream;

public class AuthenticatedYoutube implements Youtube {
    private final Youtube youtube;

    public AuthenticatedYoutube(Youtube youtube) {
        this.youtube = youtube;
    }

    @Override
    public void upload(Session user, String name, InputStream stream) throws IOException, InterruptedException, UnauthenticatedException {
        if (user == null) {
            throw new UnauthenticatedException();
        }

        youtube.upload(user, name, stream);
    }

    @Override
    public InputStream load(Session user, String name, int startSegment, int resolution) {
        // downloading without an account is ok
        return youtube.load(user, name, startSegment, resolution);
    }
}
