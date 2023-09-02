package middleware.remote;

import core.events.Action;
import core.events.EventOccurrence;
import core.model.PlayerID;
import core.server.ServerCore;
import middleware.communicators.MultiSender;
import middleware.messages_to_client.EventMessage;
import middleware.messages_to_client.GameStartedMessage;
import middleware.messages_to_client.MessageToClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Game {
    private final ServerCore core;

    private final List<PlayerID> playerIDs;
    private final Map<UserID, PlayerID> toPlayerIDMap = new HashMap<>();
    private final Map<PlayerID, UserID> fromPlayerIDMap = new HashMap<>();

    private final MultiSender<MessageToClient> sender;

    public Game(List<UserID> userIDs, MultiSender<MessageToClient> sender) {
        this.sender = sender;

        core = new ServerCore(ServerCore.newGameState(userIDs.size()),
                this::processEventOccurrence);
        playerIDs = core.state().playerManager().getPlayerIDs();

        for (int i = 0; i < playerCount(); ++i) {
            toPlayerIDMap.put(userIDs.get(i), playerIDs.get(i));
            fromPlayerIDMap.put(playerIDs.get(i), userIDs.get(i));
        }

        for (PlayerID playerID : playerIDs)
            sendMessage(playerID, new GameStartedMessage(core.state().toClientGameState(playerID)));
    }

    void sendMessage(PlayerID destination, MessageToClient message) {
        sender.sendMessage(fromPlayerID(destination), message);
    }

    private void processEventOccurrence(EventOccurrence eventOccurrence) {
        for (PlayerID playerID : eventOccurrence.recipients())
            sendMessage(playerID, new EventMessage(eventOccurrence.event()));
    }

    public int playerCount() {
        return playerIDs.size();
    }

    public PlayerID toPlayerID(UserID userID) {
        PlayerID playerID = toPlayerIDMap.get(userID);
        return Objects.requireNonNull(playerID);
    }

    public UserID fromPlayerID(PlayerID coreID) {
        UserID userID = fromPlayerIDMap.get(coreID);
        return Objects.requireNonNull(userID);
    }

    public void processAction(Action action, UserID actor) {
        core.process(action, toPlayerID(actor));
    }
}
