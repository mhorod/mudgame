package middleware.communication;

import java.io.Serializable;

public interface Sender<T extends Serializable> {
    void sendMessage(T message);
}
