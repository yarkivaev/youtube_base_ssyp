package ru.ssyp.youtube;

public class FakeUser implements User{
    @Override
    public String uniqueName() {
        return "That is null";
    }
}
