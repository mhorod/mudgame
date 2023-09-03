package middleware.network;

import middleware.communication.MessageProcessor;
import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_client.PingToClient;
import middleware.messages_to_server.MessageToServer;

public final class ServerNetworkController extends AbstractNetworkController<MessageToClient, MessageToServer> {
    public ServerNetworkController(MessageProcessor<MessageToServer> processor) {
        super(processor, MessageToServer.class);
    }

    @Override
    protected void sendPingMessage() {
        sendMessage(new PingToClient("ping", true));
    }
}
