package middleware.communicators;

import middleware.NotificationProcessor;
import middleware.UserID;

import java.io.Serializable;

public class NotifyingMessageQueue<T extends Serializable> implements MessageQueue<T> {
    private final MessageQueue<T> queue;
    private final NotificationProcessor processor;
    private final UserID source;

    public NotifyingMessageQueue(MessageQueue<T> queue, NotificationProcessor processor, UserID source) {
        this.queue = queue;
        this.processor = processor;
        this.source = source;
    }

    @Override
    public void addMessage(T message) {
        queue.addMessage(message);
        processor.processNotification(source);
    }

    @Override
    public boolean hasMessage() {
        return queue.hasMessage();
    }

    @Override
    public T removeMessage() {
        return queue.removeMessage();
    }

    @Override
    public T takeMessage() throws InterruptedException {
        return queue.takeMessage();
    }
}
