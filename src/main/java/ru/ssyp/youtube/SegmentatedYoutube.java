package ru.ssyp.youtube;

import java.io.InputStream;
public class SegmentatedYoutube implements Youtube {

    private final Youtube youtube;

    public SegmentatedYoutube(Youtube youtube) {
        this.youtube = youtube;
    }

    @Override
    public void upload(Session user, String name, InputStream stream) {
        youtube.upload(user, name, stream);
    }

    @Override
    public InputStream load(Session user, String name, Double startSec) {
        youtube.load(user, name, startSec);



        return null;
    }

    public static void main() {
        
    }
}
