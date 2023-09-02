package middleware.communicators;

import middleware.remote.UserID;

import java.io.Serializable;

public interface NotificationProcessor<T extends Serializable> {
    void processMessage(UserID source, T message);
}
