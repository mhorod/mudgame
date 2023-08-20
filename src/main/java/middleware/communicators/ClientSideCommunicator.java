package middleware.communicators;

import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;

public class ClientSideCommunicator implements Sender<MessageToServer>, Receiver<MessageToClient> {
    private final Sender<MessageToServer> sender;
    private final Receiver<MessageToClient> receiver;

    public ClientSideCommunicator(Sender<MessageToServer> sender, Receiver<MessageToClient> receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public void sendMessage(MessageToServer message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean hasMessage() {
        return receiver.hasMessage();
    }

    @Override
    public MessageToClient removeMessage() {
        return receiver.removeMessage();
    }

    @Override
    public MessageToClient takeMessage() throws InterruptedException {
        return receiver.takeMessage();
    }
}
