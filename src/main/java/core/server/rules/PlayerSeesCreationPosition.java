package core.server.rules;

import core.entities.events.CreateEntity;
import core.events.Action;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;

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
