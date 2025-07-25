package ru.ssyp.youtube.channel;

import ru.ssyp.youtube.video.Video;

public interface Channel {
    ChannelInfo channelInfo();

    boolean checkSubscription(int userId) throws InvalidUserIdException;

    void subscribe(int userId) throws InvalidUserIdException, AlreadySubscribedException;

    void unsubscribe(int userId) throws NotSubscribedException, InvalidUserIdException;

    Video[] videos(int startId, int amount);
}
