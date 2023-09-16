package mudgame.server.rules.movement;

import core.entities.EntityBoardView;
import mudgame.controls.actions.Action;
import core.model.EntityID;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.MoveEntity;
import mudgame.server.rules.ActionRule;

@RequiredArgsConstructor
public final class PlayerOwnsMovedEntity implements ActionRule {
    private final EntityBoardView boardView;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof MoveEntity moveEntity)
            return ownsEntity(actor, moveEntity.entityID());
        else
            return true;
    }

    private boolean ownsEntity(PlayerID player, EntityID entityID) {
        return boardView.findEntityByID(entityID).owner().equals(player);
    }
}
