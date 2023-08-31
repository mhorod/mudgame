package middleware.communicators;

import java.io.Serializable;

public interface Receiver<T extends Serializable> {
    boolean hasMessage();

    T removeMessage();

    T takeMessage() throws InterruptedException;
}
