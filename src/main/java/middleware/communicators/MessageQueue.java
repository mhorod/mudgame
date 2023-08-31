package middleware.communicators;

import java.io.Serializable;

public interface MessageQueue<T extends Serializable> extends Receiver<T> {
    void addMessage(T message);
}
