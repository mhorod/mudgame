package mudgame.integration.utils;

import core.claiming.ClaimedAreaView;
import core.entities.EntityBoardView;
import core.fogofwar.FogOfWarView;
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
            FogOfWarView fow,
            TerrainView terrain,
            ClaimedAreaView claimedArea
    ) {
        return MudServerCore.defaultRules(
                turnView,
                entityBoard,
                fow,
                terrain,
                claimedArea
        );
    }
}
