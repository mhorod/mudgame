package middleware;

import middleware.communicators.MessageQueue;
import middleware.communicators.NotifyingMessageQueue;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotifyingMessageQueueTest {
    @SuppressWarnings("unchecked")
    private <T extends Serializable> MessageQueue<T> mockQueue() {
        return (MessageQueue<T>) mock(MessageQueue.class);
    }

    @Test
    void add_message_creates_notification_and_forwards() throws Throwable {
        // given
        UserID source = new UserID(0);
        Integer message = 420;
        MessageQueue<Integer> queue = mockQueue();
        NotificationProcessor processor = mock(NotificationProcessor.class);
        NotifyingMessageQueue<Integer> notifyingQueue = new NotifyingMessageQueue<>(queue, processor, source);

        // when
        notifyingQueue.addMessage(message);

        // then
        verify(processor, times(1)).processNotification(source);
        verify(queue, times(1)).addMessage(message);
        verify(queue, times(0)).hasMessage();
        verify(queue, times(0)).removeMessage();
        verify(queue, times(0)).takeMessage();
    }

    @Test
    void has_message_forwards_true() throws Throwable {
        // given
        MessageQueue<Integer> queue = mockQueue();
        NotificationProcessor processor = mock(NotificationProcessor.class);
        NotifyingMessageQueue<Integer> notifyingQueue = new NotifyingMessageQueue<>(queue, processor, new UserID(0));

        // when
        when(queue.hasMessage()).thenReturn(true);
        boolean hasMessage = notifyingQueue.hasMessage();

        // then
        assertThat(hasMessage).isTrue();
        verify(processor, times(0)).processNotification(any());
        verify(queue, times(0)).addMessage(any());
        verify(queue, times(1)).hasMessage();
        verify(queue, times(0)).removeMessage();
        verify(queue, times(0)).takeMessage();
    }

    @Test
    void has_message_forwards_false() throws Throwable {
        // given
        MessageQueue<Integer> queue = mockQueue();
        NotificationProcessor processor = mock(NotificationProcessor.class);
        NotifyingMessageQueue<Integer> notifyingQueue = new NotifyingMessageQueue<>(queue, processor, new UserID(0));

        // when
        when(queue.hasMessage()).thenReturn(false);
        boolean hasMessage = notifyingQueue.hasMessage();

        // then
        assertThat(hasMessage).isFalse();
        verify(processor, times(0)).processNotification(any());
        verify(queue, times(0)).addMessage(any());
        verify(queue, times(1)).hasMessage();
        verify(queue, times(0)).removeMessage();
        verify(queue, times(0)).takeMessage();
    }

    @Test
    void remove_message_forwards() throws Throwable {
        // given
        MessageQueue<Integer> queue = mockQueue();
        NotificationProcessor processor = mock(NotificationProcessor.class);
        NotifyingMessageQueue<Integer> notifyingQueue = new NotifyingMessageQueue<>(queue, processor, new UserID(0));

        // when
        when(queue.removeMessage()).thenReturn(400);
        Integer message = notifyingQueue.removeMessage();

        // then
        assertThat(message).isEqualTo(400);
        verify(processor, times(0)).processNotification(any());
        verify(queue, times(0)).addMessage(any());
        verify(queue, times(0)).hasMessage();
        verify(queue, times(1)).removeMessage();
        verify(queue, times(0)).takeMessage();
    }

    @Test
    void take_message_forwards() throws Throwable {
        // given
        MessageQueue<Integer> queue = mockQueue();
        NotificationProcessor processor = mock(NotificationProcessor.class);
        NotifyingMessageQueue<Integer> notifyingQueue = new NotifyingMessageQueue<>(queue, processor, new UserID(0));

        // when
        when(queue.takeMessage()).thenReturn(null);
        Integer message = notifyingQueue.takeMessage();

        // then
        assertThat(message).isNull();
        verify(processor, times(0)).processNotification(any());
        verify(queue, times(0)).addMessage(any());
        verify(queue, times(0)).hasMessage();
        verify(queue, times(0)).removeMessage();
        verify(queue, times(1)).takeMessage();
    }

    @Test
    void take_message_forwards_exception() throws Throwable {
        // given
        MessageQueue<Integer> queue = mockQueue();
        NotificationProcessor processor = mock(NotificationProcessor.class);
        NotifyingMessageQueue<Integer> notifyingQueue = new NotifyingMessageQueue<>(queue, processor, new UserID(0));

        // when
        when(queue.takeMessage()).thenThrow(new InterruptedException());
        Throwable throwable = catchThrowable(notifyingQueue::takeMessage);

        // then
        assertThat(throwable).isInstanceOf(InterruptedException.class);
        verify(processor, times(0)).processNotification(any());
        verify(queue, times(0)).addMessage(any());
        verify(queue, times(0)).hasMessage();
        verify(queue, times(0)).removeMessage();
        verify(queue, times(1)).takeMessage();
    }
}
