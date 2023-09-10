package mudgame.integration.utils;

import core.event.Action;
import core.event.Event;
import core.event.EventOccurrence;
import core.model.PlayerID;
import mudgame.client.ClientGameState;
import mudgame.client.MudClientCore;
import mudgame.events.EventOccurrenceObserver;
import mudgame.server.MudServerCore;
import mudgame.server.ServerGameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toMap;

public class ScenarioGame {

    private class EventSender implements EventOccurrenceObserver {
        @Override
        public void receive(EventOccurrence eventOccurrence) {
            for (PlayerID player : eventOccurrence.recipients()) {
                clientCores.get(player).receive(eventOccurrence.event());
                receivedEvents.get(player).add(eventOccurrence.event());
            }
        }
    }

    private final MudServerCore serverCore;
    private final Map<PlayerID, MudClientCore> clientCores;
    Map<PlayerID, List<Event>> receivedEvents;

    public ScenarioGame(ServerGameState initialState) {
        EventSender sender = new EventSender();
        serverCore = new MudServerCore(initialState, sender);
        clientCores = initialState.playerManager()
                .getPlayerIDs()
                .stream()
                .collect(toMap(
                        p -> p,
                        p -> new MudClientCore(initialState.toClientGameState(p))
                ));
        receivedEvents = initialState.playerManager()
                .getPlayerIDs()
                .stream()
                .collect(toMap(
                        p -> p,
                        p -> new ArrayList<>()
                ));
    }

    public ScenarioGame act(PlayerID player, Action... actions) {
        for (Action a : actions)
            serverCore.process(a, player);
        return this;
    }

    public ScenarioResult finish() {
        return new ScenarioResult(
                serverCore.state(),
                states(clientCores),
                receivedEvents
        );
    }

    private Map<PlayerID, ClientGameState> states(Map<PlayerID, MudClientCore> clientCores) {
        return clientCores.entrySet().stream()
                .collect(toMap(
                        Entry::getKey,
                        e -> e.getValue().state()
                ));
    }

}