package mudgame.server.rules.movement;

import mudgame.controls.actions.Action;
import core.model.PlayerID;
import core.pathfinder.Pathfinder;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.MoveEntity;
import mudgame.server.rules.ActionRule;

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
