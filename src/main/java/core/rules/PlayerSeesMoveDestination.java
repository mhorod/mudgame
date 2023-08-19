package core.rules;

import core.entities.events.MoveEntity;
import core.events.Event.Action;
import core.fogofwar.FogOfWarView;
import core.model.PlayerID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerSeesMoveDestination implements ActionRule
{
    private final FogOfWarView fow;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor)
    {
        if (action instanceof MoveEntity moveEntity)
            return fow.isVisible(moveEntity.destination(), actor);
        else
            return true;
    }

}
