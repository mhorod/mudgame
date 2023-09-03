package middleware.communication;

import middleware.remote.UserID;

import java.io.Serializable;

public final class NotifyingMessageProcessor<T extends Serializable> implements MessageProcessor<T> {
    private final UserID source;
    private final NotificationProcessor<T> processor;

    public NotifyingMessageProcessor(UserID source, NotificationProcessor<T> processor) {
        this.source = source;
        this.processor = processor;
    }

    @Override
    public void processMessage(T message) {
        processor.processMessage(source, message);
    }
}
