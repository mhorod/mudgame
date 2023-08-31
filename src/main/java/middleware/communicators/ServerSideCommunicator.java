package middleware.communicators;

import middleware.messages_to_client.MessageToClient;
import middleware.messages_to_server.MessageToServer;

// TODO this should implement only sender (?)
public final class ServerSideCommunicator implements Sender<MessageToClient>, Receiver<MessageToServer> {
    private final Sender<MessageToClient> sender;
    private final Receiver<MessageToServer> receiver;

    public ServerSideCommunicator(Sender<MessageToClient> sender, Receiver<MessageToServer> receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public void sendMessage(MessageToClient message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean hasMessage() {
        return receiver.hasMessage();
    }

    @Override
    public MessageToServer removeMessage() {
        return receiver.removeMessage();
    }

    @Override
    public MessageToServer takeMessage() throws InterruptedException {
        return receiver.takeMessage();
    }
}
