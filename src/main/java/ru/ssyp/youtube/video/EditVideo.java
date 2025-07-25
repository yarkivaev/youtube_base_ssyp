package ru.ssyp.youtube.video;

import java.io.InputStream;
import java.util.Optional;

public class EditVideo {

    public final Optional<String> name;
    public final Optional<String> description;
    public final Optional<InputStream> data;


    public EditVideo(Optional<String> name, Optional<String> description, Optional<InputStream> data) {
        this.name = name;
        this.description = description;
        this.data = data;
    }
}
