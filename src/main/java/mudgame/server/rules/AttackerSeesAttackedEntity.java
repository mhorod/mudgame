package mudgame.server.rules;

import core.entities.EntityBoardView;
import core.event.Action;
import core.fogofwar.FogOfWarView;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.AttackEntityAction;

@RequiredArgsConstructor
public class AttackerSeesAttackedEntity implements ActionRule {
    private final EntityBoardView entityBoard;
    private final FogOfWarView fow;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof AttackEntityAction a)
            return seesEntity(a.attacked(), actor);
        else
            return true;
    }

    private boolean seesEntity(EntityID attacked, PlayerID actor) {
        Position position = entityBoard.entityPosition(attacked);
        return fow.isVisible(position, actor);
    }
}
