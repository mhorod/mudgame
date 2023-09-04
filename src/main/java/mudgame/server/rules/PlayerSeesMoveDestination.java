package mudgame.server.rules;

import core.entities.events.MoveEntity;
import mudgame.events.Event.Action;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;

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
