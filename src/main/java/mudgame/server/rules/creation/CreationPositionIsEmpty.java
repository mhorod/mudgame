package mudgame.server.rules.creation;

import core.entities.EntityBoardView;
import mudgame.controls.actions.Action;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.CreateEntity;
import mudgame.server.rules.ActionRule;

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
