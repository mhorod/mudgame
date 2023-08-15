package core.rules;

import core.entities.EntityBoardView;
import core.events.Event.Action;
import core.events.Event.MoveEntity;
import core.id.EntityID;
import core.id.PlayerID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerOwnsMovedEntity implements ActionRule
{
    private final EntityBoardView boardView;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor)
    {
        if (action instanceof MoveEntity moveEntity)
            return ownsEntity(actor, moveEntity.entityID());
        else
            return true;
    }

    private boolean ownsEntity(PlayerID player, EntityID entityID)
    {
        return boardView.findEntityByID(entityID).owner().equals(player);
    }
}
