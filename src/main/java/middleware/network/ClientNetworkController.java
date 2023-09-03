package middleware.network;

import middleware.communication.MessageProcessor;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;
import middleware.messages_to_server.PingToServer;

public final class ClientNetworkController extends AbstractNetworkController<MessageToServer, MessageToClient> {
    public ClientNetworkController(MessageProcessor<MessageToClient> processor) {
        super(processor, MessageToClient.class);
    }

    @Override
    protected void sendPingMessage() {
        sendMessage(new PingToServer("ping", true));
    }
}
