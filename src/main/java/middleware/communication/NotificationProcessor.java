package middleware.communication;

import middleware.model.UserID;

import java.io.Serializable;

public interface NotificationProcessor<T extends Serializable> {
    void processMessage(UserID source, T message);
}
