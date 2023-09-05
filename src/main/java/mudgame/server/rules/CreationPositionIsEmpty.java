package mudgame.server.rules;

import core.entities.EntityBoardView;
import core.event.Action;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.CreateEntity;

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
