package ru.ssyp.youtube;

import java.io.InputStream;

public class FakeStorage implements Storage{

    @Override
    public void upload(String name, InputStream inputStream) {
        System.out.println(name);
    }

    @Override
    public InputStream download(String name) {
        return null;
    }
}
