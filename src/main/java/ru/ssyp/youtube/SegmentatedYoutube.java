package ru.ssyp.youtube;

public class SegmentatedYoutube implements Youtube {

    private final Youtube youtube;
    
    @Override
    public void upload(User user, String name, InputStream stream) {
        youtube.upload(user, name, stream);
    }

    @Override
    public InputStream load(User user, String name, Double startSec) {
        youtube.load(user, name, startSec);




    }

    public static void main() {
        
    }
}
