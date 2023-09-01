package middleware.communicators;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class ProcessingMessageQueue<T extends Serializable> implements MessageQueue<T>, MessageProcessor<T> {
    private final BlockingQueue<T> incomingMessageQueue = new LinkedBlockingQueue<>();

    @Override
    public boolean hasMessage() {
        return !incomingMessageQueue.isEmpty();
    }

    @Override
    public T removeMessage() {
        return incomingMessageQueue.remove();
    }

    @Override
    public void processMessage(T message) {
        incomingMessageQueue.add(message);
    }
}
