package middleware.communicators;

import middleware.UserID;

import java.io.Serializable;

public interface MultiSender<T extends Serializable> {
    void sendMessage(UserID destination, T message);
}
