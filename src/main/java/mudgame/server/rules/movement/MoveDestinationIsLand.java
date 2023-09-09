package mudgame.server.rules.movement;

import core.event.Action;
import core.model.PlayerID;
import core.terrain.TerrainView;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.MoveEntity;
import mudgame.server.rules.ActionRule;

import static core.terrain.model.TerrainType.LAND;

@RequiredArgsConstructor
public class MoveDestinationIsLand implements ActionRule {
    private final TerrainView terrain;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof MoveEntity a)
            return terrain.terrainAt(a.destination()).equals(LAND);
        else
            return true;
    }
}
