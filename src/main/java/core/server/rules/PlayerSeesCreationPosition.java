package core.server.rules;

import core.entities.events.CreateEntity;
import core.events.Event.Action;
import core.fogofwar.FogOfWarView;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PlayerSeesCreationPosition implements ActionRule {
    private final FogOfWarView fow;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof CreateEntity moveEntity)
            return fow.isVisible(moveEntity.position(), actor);
        else
            return true;
    }

}
