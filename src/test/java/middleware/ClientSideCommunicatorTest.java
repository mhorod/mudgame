package middleware;

import middleware.communicators.ClientSideCommunicator;
import middleware.communicators.Receiver;
import middleware.communicators.Sender;
import middleware.messages_to_client.ErrorMessage;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.ActionMessage;
import middleware.messages_to_server.MessageToServer;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientSideCommunicatorTest {
    @SuppressWarnings("unchecked")
    private <T extends Serializable> Sender<T> mockSender() {
        return (Sender<T>) mock(Sender.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends Serializable> Receiver<T> mockReceiver() {
        return (Receiver<T>) mock(Receiver.class);
    }

    @Test
    void send_message_forwards() throws Throwable {
        // given
        MessageToServer message = new ActionMessage(null);
        Sender<MessageToServer> sender = mockSender();
        Receiver<MessageToClient> receiver = mockReceiver();
        ClientSideCommunicator communicator = new ClientSideCommunicator(sender, receiver);

        // when
        communicator.sendMessage(message);

        // then
        verify(sender, times(1)).sendMessage(message);
        verify(receiver, times(0)).hasMessage();
        verify(receiver, times(0)).removeMessage();
        verify(receiver, times(0)).takeMessage();
    }

    @Test
    void has_message_forwards_true() throws Throwable {
        // given
        Sender<MessageToServer> sender = mockSender();
        Receiver<MessageToClient> receiver = mockReceiver();
        ClientSideCommunicator communicator = new ClientSideCommunicator(sender, receiver);

        // when
        when(receiver.hasMessage()).thenReturn(true);
        boolean hasMessage = communicator.hasMessage();

        // then
        assertThat(hasMessage).isTrue();
        verify(sender, times(0)).sendMessage(any());
        verify(receiver, times(1)).hasMessage();
        verify(receiver, times(0)).removeMessage();
        verify(receiver, times(0)).takeMessage();
    }

    @Test
    void has_message_forwards_false() throws Throwable {
        // given
        Sender<MessageToServer> sender = mockSender();
        Receiver<MessageToClient> receiver = mockReceiver();
        ClientSideCommunicator communicator = new ClientSideCommunicator(sender, receiver);

        // when
        when(receiver.hasMessage()).thenReturn(false);
        boolean hasMessage = communicator.hasMessage();

        // then
        assertThat(hasMessage).isFalse();
        verify(sender, times(0)).sendMessage(any());
        verify(receiver, times(1)).hasMessage();
        verify(receiver, times(0)).removeMessage();
        verify(receiver, times(0)).takeMessage();
    }

    @Test
    void remove_message_forwards() throws Throwable {
        // given
        Sender<MessageToServer> sender = mockSender();
        Receiver<MessageToClient> receiver = mockReceiver();
        ClientSideCommunicator communicator = new ClientSideCommunicator(sender, receiver);

        // when
        when(receiver.removeMessage()).thenReturn(null);
        MessageToClient message = communicator.removeMessage();

        // then
        assertThat(message).isNull();
        verify(sender, times(0)).sendMessage(any());
        verify(receiver, times(0)).hasMessage();
        verify(receiver, times(1)).removeMessage();
        verify(receiver, times(0)).takeMessage();
    }

    @Test
    void take_message_forwards() throws Throwable {
        // given
        Sender<MessageToServer> sender = mockSender();
        Receiver<MessageToClient> receiver = mockReceiver();
        ClientSideCommunicator communicator = new ClientSideCommunicator(sender, receiver);

        // when
        when(receiver.takeMessage()).thenReturn(new ErrorMessage(""));
        MessageToClient message = communicator.takeMessage();

        // then
        assertThat(message).isEqualTo(new ErrorMessage(""));
        verify(sender, times(0)).sendMessage(any());
        verify(receiver, times(0)).hasMessage();
        verify(receiver, times(0)).removeMessage();
        verify(receiver, times(1)).takeMessage();
    }

    @Test
    void take_message_forwards_exception() throws Throwable {
        // given
        Sender<MessageToServer> sender = mockSender();
        Receiver<MessageToClient> receiver = mockReceiver();
        ClientSideCommunicator communicator = new ClientSideCommunicator(sender, receiver);

        // when
        when(receiver.takeMessage()).thenThrow(new InterruptedException());
        Throwable throwable = catchThrowable(communicator::takeMessage);

        // then
        assertThat(throwable).isInstanceOf(InterruptedException.class);
        verify(sender, times(0)).sendMessage(any());
        verify(receiver, times(0)).hasMessage();
        verify(receiver, times(0)).removeMessage();
        verify(receiver, times(1)).takeMessage();
    }
}
