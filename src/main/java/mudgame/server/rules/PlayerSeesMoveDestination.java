package mudgame.server.rules;

import core.event.Action;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.MoveEntity;

@RequiredArgsConstructor
public final class PlayerSeesMoveDestination implements ActionRule {
    private final FogOfWar fow;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof MoveEntity moveEntity)
            return fow.isVisible(moveEntity.destination(), actor);
        else
            return true;
    }

}
