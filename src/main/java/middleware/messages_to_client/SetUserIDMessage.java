package middleware.messages_to_client;

import middleware.model.UserID;
import middleware.remote.RemoteNetworkClient;

public record SetUserIDMessage(UserID userID) implements MessageToClient {
    @Override
    public void execute(RemoteNetworkClient client) {
        client.setUserID(userID);
    }
}
