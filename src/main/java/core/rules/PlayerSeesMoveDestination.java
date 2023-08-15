package core.rules;

import core.FogOfWarView;
import core.Position;
import core.events.Event.Action;
import core.events.Event.MoveEntity;
import core.id.PlayerID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerSeesMoveDestination implements ActionRule
{
    private final FogOfWarView fow;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor)
    {
        if (action instanceof MoveEntity moveEntity)
            return seesDestination(actor, moveEntity.destination());
        else
            return true;
    }

    private boolean seesDestination(PlayerID player, Position position)
    {
        return fow.isVisible(position, player);
    }
}
