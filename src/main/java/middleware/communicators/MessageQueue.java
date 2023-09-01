package middleware.communicators;

import java.io.Serializable;

public interface MessageQueue<T extends Serializable> {
    boolean hasMessage();

    T removeMessage();
}
