package middleware;

import core.client.ClientCore;
import core.events.Event;
import middleware.messages_to_client.EventMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageToClientTest {
    @Test
    void event_message_core_is_null() {
        // given
        Client client = mock(Client.class);
        Event event = mock(Event.class);
        EventMessage message = new EventMessage(event);

        // when
        message.execute(client);
    }

    @Test
    void event_message_core_is_not_null() {
        // given
        Client client = mock(Client.class);
        ClientCore core = mock(ClientCore.class);
        Event event = mock(Event.class);
        EventMessage message = new EventMessage(event);

        // when
        when(client.getCore()).thenReturn(core);
        message.execute(client);

        // then
        verifyNoInteractions(core);
        verify(client).receiveEvent(event);
    }
}
