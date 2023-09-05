package mudgame.server.rules;

import core.event.Action;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.CreateEntity;

@RequiredArgsConstructor
public final class PlayerSeesCreationPosition implements ActionRule {
    private final FogOfWar fow;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof CreateEntity moveEntity)
            return fow.isVisible(moveEntity.position(), actor);
        else
            return true;
    }

}
