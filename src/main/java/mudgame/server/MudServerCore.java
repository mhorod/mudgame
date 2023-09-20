package mudgame.server;

import core.model.PlayerID;
import lombok.extern.slf4j.Slf4j;
import mudgame.client.ClientGameState;
import mudgame.controls.actions.Action;
import mudgame.server.actions.ActionProcessor;
import mudgame.server.state.ServerState;

import java.util.List;


@Slf4j
public final class MudServerCore {
    private final ServerState state;
    private final ActionProcessor actionProcessor;

    public MudServerCore(ServerState state, EventOccurrenceObserver eventOccurrenceObserver) {
        this.state = state;
        this.actionProcessor = new ActionProcessor(state, eventOccurrenceObserver);
    }

    public void process(Action action, PlayerID actor) {
        if (action != null)
            actionProcessor.process(action, actor);
    }

    public List<PlayerID> players() {
        return state.turnManager().players();
    }

    public ServerState state() {
        return state;
    }

    public ClientGameState clientState(PlayerID playerID) {
        return state.toClientGameState(playerID);
    }
}
