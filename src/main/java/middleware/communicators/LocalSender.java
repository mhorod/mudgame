package middleware.communicators;

import java.io.Serializable;

public final class LocalSender<T extends Serializable> implements Sender<T> {
    private final MessageQueue<T> queue;

    public LocalSender(MessageQueue<T> queue) {
        this.queue = queue;
    }

    @Override
    public void sendMessage(T message) {
        queue.addMessage(message);
    }
}
