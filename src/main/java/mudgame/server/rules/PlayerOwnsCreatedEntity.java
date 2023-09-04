package mudgame.server.rules;

import core.model.PlayerID;
import mudgame.controls.events.CreateEntity;
import mudgame.events.Action;

public final class PlayerOwnsCreatedEntity implements ActionRule {
    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof CreateEntity createEntity)
            return createEntity.owner().equals(actor);
        else
            return true;
    }
}
