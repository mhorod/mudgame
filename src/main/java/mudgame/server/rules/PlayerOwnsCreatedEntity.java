package mudgame.server.rules;

import core.entities.events.CreateEntity;
import mudgame.events.Event.Action;
import core.model.PlayerID;

public final class PlayerOwnsCreatedEntity implements ActionRule {
    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof CreateEntity createEntity)
            return createEntity.owner().equals(actor);
        else
            return true;
    }
}
