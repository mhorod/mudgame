package mudgame.integration.utils;

import core.entities.EntityBoardView;
import core.fogofwar.FogOfWar;
import core.terrain.TerrainView;
import core.turns.TurnView;
import mudgame.server.rules.ActionRule;

import java.util.List;

public interface RuleProvider {
    List<ActionRule> rules(
            TurnView playerManager,
            EntityBoardView entityBoard,
            FogOfWar fow,
            TerrainView terrain
    );
}