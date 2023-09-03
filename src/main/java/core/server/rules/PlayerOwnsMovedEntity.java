package core.server.rules;

import core.entities.EntityBoardView;
import core.entities.events.MoveEntity;
import core.events.Action;
import core.model.EntityID;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;

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
