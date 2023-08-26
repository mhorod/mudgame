package core.rules;

import core.entities.EntityBoardView;
import core.entities.events.CreateEntity;
import core.events.model.Event.Action;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreationPositionIsEmpty implements ActionRule {
    private final EntityBoardView boardView;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof CreateEntity moveEntity)
            return boardView.entitiesAt(moveEntity.position()).isEmpty();
        else
            return true;
    }

}
