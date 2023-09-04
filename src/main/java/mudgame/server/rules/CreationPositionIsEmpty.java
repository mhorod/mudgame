package mudgame.server.rules;

import core.entities.EntityBoardView;
import core.entities.events.CreateEntity;
import mudgame.events.Event.Action;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CreationPositionIsEmpty implements ActionRule {
    private final EntityBoardView boardView;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof CreateEntity moveEntity)
            return boardView.entitiesAt(moveEntity.position()).isEmpty();
        else
            return true;
    }

}
