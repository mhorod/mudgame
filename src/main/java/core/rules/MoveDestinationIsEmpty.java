package core.rules;

import core.entities.EntityBoardView;
import core.events.Event.Action;
import core.events.Event.MoveEntity;
import core.id.PlayerID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MoveDestinationIsEmpty implements ActionRule
{
    private final EntityBoardView boardView;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor)
    {
        if (action instanceof MoveEntity moveEntity)
            return boardView.entitiesAt(moveEntity.destination()).isEmpty();
        else
            return true;
    }
}
