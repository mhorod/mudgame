package middleware.communicators;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueueImpl<T extends Serializable> implements MessageQueue<T> {
    final BlockingQueue<T> incomingMessageQueue = new LinkedBlockingQueue<>();

    @Override
    public void addMessage(T message) {
        incomingMessageQueue.add(message);
    }

    @Override
    public boolean hasMessage() {
        return !incomingMessageQueue.isEmpty();
    }

    @Override
    public T removeMessage() {
        return incomingMessageQueue.remove();
    }

    public T takeMessage() throws InterruptedException {
        return incomingMessageQueue.take();
    }
}
