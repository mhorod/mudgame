package mudgame.server.rules;

import core.event.Action;
import core.model.PlayerID;
import core.pathfinder.Pathfinder;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.MoveEntity;

@RequiredArgsConstructor
public class MoveDestinationIsReachable implements ActionRule {
    private final Pathfinder pathfinder;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof MoveEntity m)
            return pathfinder.isReachable(m.entityID(), m.destination());
        else
            return true;
    }
}
