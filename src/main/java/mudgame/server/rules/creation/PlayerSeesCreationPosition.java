package mudgame.server.rules.creation;

import mudgame.controls.actions.Action;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.CreateEntity;
import mudgame.server.rules.ActionRule;

@RequiredArgsConstructor
public final class PlayerSeesCreationPosition implements ActionRule {
    private final FogOfWar fow;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof CreateEntity moveEntity)
            return fow.playerSees(moveEntity.position(), actor);
        else
            return true;
    }

}
