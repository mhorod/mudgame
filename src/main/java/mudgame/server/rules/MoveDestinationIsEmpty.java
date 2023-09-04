package mudgame.server.rules;

import core.entities.EntityBoardView;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.events.MoveEntity;
import core.event.Action;

@RequiredArgsConstructor
public final class MoveDestinationIsEmpty implements ActionRule {
    private final EntityBoardView boardView;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof MoveEntity moveEntity)
            return boardView.entitiesAt(moveEntity.destination()).isEmpty();
        else
            return true;
    }
}
