package mudgame.server.rules.movement;

import core.event.Action;
import core.fogofwar.FogOfWarView;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.MoveEntity;
import mudgame.server.rules.ActionRule;

@RequiredArgsConstructor
public final class PlayerSeesMoveDestination implements ActionRule {
    private final FogOfWarView fow;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof MoveEntity moveEntity)
            return fow.playerSees(moveEntity.destination(), actor);
        else
            return true;
    }

}
