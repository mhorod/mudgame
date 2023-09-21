package testutils.integration.utils;

import core.claiming.PlayerClaimedArea;
import core.entities.EntityBoard;
import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import core.resources.PlayerResourceManager;
import mudgame.client.ClientGameState;
import mudgame.controls.events.Event;
import mudgame.server.state.ServerState;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record ScenarioResult(
        ServerState serverState,
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
        return serverState.turnManager().currentPlayer();
    }

    public PlayerID clientTurn(PlayerID player) {
        return clientStates.get(player).turnManager().currentPlayer();
    }

    public ClientGameState clientState(PlayerID player) {
        return clientStates().get(player);
    }

    public PlayerClaimedArea clientClaimedArea(PlayerID player) {
        return clientStates.get(player).claimedArea();
    }

    public PlayerClaimedArea serverClaimedArea(PlayerID player) {
        return serverState.claimedArea().mask(serverFow(player), serverState.terrain());
    }

    public PlayerResourceManager clientResources(PlayerID player) {
        return clientStates.get(player).resourceManager();
    }

    public PlayerResourceManager serverResources(PlayerID player) {
        return serverState.resourceManager().playerResources(player);
    }

    public boolean clientIsGameOver(PlayerID player) {
        return clientStates.get(player).gameOverCondition().isGameOver();
    }

    public boolean serverIsGameOver() {
        return serverState.gameOverCondition().isGameOver();
    }

    public Optional<List<PlayerID>> clientWinners(PlayerID player) {
        return clientStates.get(player).gameOverCondition().winners();
    }

    public Optional<List<PlayerID>> serverWinners() {
        return serverState.gameOverCondition().winners();
    }
}
