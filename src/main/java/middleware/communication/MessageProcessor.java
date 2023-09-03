package middleware.communication;

import java.io.Serializable;

public interface MessageProcessor<T extends Serializable> {
    void processMessage(T message);
}
