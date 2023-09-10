package mudgame.integration.utils;

import core.entities.EntityBoard;
import core.event.Event;
import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import mudgame.client.ClientGameState;
import mudgame.server.ServerGameState;

import java.util.List;
import java.util.Map;

public record ScenarioResult(
        ServerGameState serverState,
        Map<PlayerID, ClientGameState> clientStates,
        Map<PlayerID, List<Event>> receivedEvents
) {
    public List<Event> clientEvents(PlayerID player) {
        return receivedEvents.get(player);
    }

    public PlayerFogOfWar serverFow(PlayerID player) {
        return serverState.fogOfWar().playerFogOfWar(player);
    }

    public PlayerFogOfWar clientFow(PlayerID player) {
        return clientStates.get(player).fogOfWar();
    }

    public EntityBoard serverEntityBoard(PlayerID player) {
        return serverState.entityBoard().applyFogOfWar(serverFow(player));
    }

    public EntityBoard clientEntityBoard(PlayerID player) {
        return clientStates.get(player).entityBoard();
    }

    public List<PlayerID> players() {
        return clientStates().keySet().stream().toList();
    }

    public PlayerID serverTurn() {
        return serverState.playerManager().getCurrentPlayer();
    }

    public PlayerID clientTurn(PlayerID player) {
        return clientStates.get(player).playerManager().getCurrentPlayer();
    }

    public ClientGameState clientState(PlayerID player) {
        return clientStates().get(player);
    }
}