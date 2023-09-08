package mudgame.integration.utils;

import core.entities.EntityBoardView;
import core.fogofwar.FogOfWar;
import core.terrain.TerrainView;
import core.turns.TurnView;
import mudgame.server.MudServerCore;
import mudgame.server.rules.ActionRule;

import java.util.List;

public class DefaultRules implements RuleProvider {
    @Override
    public List<ActionRule> rules(
            TurnView turnView,
            EntityBoardView entityBoard,
            FogOfWar fow,
            TerrainView terrain
    ) {
        return MudServerCore.defaultRules(
                turnView,
                entityBoard,
                fow,
                terrain
        );
    }
}
