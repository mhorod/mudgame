package mudgame.server.rules.creation;

import mudgame.controls.actions.Action;
import core.model.PlayerID;
import mudgame.controls.actions.CreateEntity;
import mudgame.server.rules.ActionRule;


public final class PlayerOwnsCreatedEntity implements ActionRule {
    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof CreateEntity createEntity)
            return createEntity.owner().equals(actor);
        else
            return true;
    }
}
