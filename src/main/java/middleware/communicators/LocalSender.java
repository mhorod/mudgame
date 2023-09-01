package middleware.communicators;

import java.io.Serializable;

public final class LocalSender<T extends Serializable> implements Sender<T> {
    private final MessageProcessor<T> processor;

    public LocalSender(MessageProcessor<T> processor) {
        this.processor = processor;
    }

    @Override
    public void sendMessage(T message) {
        processor.processMessage(message);
    }
}
