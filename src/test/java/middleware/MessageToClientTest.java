package middleware;

import core.event.Event;
import middleware.messages_to_client.EventMessage;
import mudgame.client.MudClientCore;
import org.junit.jupiter.api.Test;

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
        MudClientCore core = mock(MudClientCore.class);
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
