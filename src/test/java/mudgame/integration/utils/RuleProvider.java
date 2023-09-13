package mudgame.integration.utils;

import core.claiming.ClaimedAreaView;
import core.entities.EntityBoardView;
import core.fogofwar.FogOfWarView;
import core.resources.ResourcesView;
import core.terrain.TerrainView;
import core.turns.TurnView;
import mudgame.server.rules.ActionRule;

import java.util.List;

public interface RuleProvider {
    List<ActionRule> rules(
            TurnView playerManager,
            EntityBoardView entityBoard,
            FogOfWarView fow,
            ResourcesView resources,
            TerrainView terrain,
            ClaimedAreaView claimedArea
    );
}