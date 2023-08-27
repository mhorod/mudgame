package core.rules;

import core.entities.events.CreateEntity;
import core.events.model.Event.Action;
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
