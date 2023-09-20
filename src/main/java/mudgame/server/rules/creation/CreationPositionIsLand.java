package mudgame.server.rules.creation;

import mudgame.controls.actions.Action;
import core.model.PlayerID;
import core.terrain.TerrainView;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.CreateEntity;
import mudgame.server.rules.ActionRule;

import static core.terrain.model.TerrainType.LAND;

@RequiredArgsConstructor
public class CreationPositionIsLand implements ActionRule {
    private final TerrainView terrain;

    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof CreateEntity a)
            return terrain.terrainAt(a.position()).equals(LAND);
        else
            return true;
    }
}
