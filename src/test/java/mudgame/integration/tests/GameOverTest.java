package mudgame.integration.tests;

import mudgame.integration.utils.RectangleTerrain;
import mudgame.integration.utils.Scenario;
import mudgame.integration.utils.ScenarioResult;
import org.junit.jupiter.api.Test;

import static core.resources.ResourceType.MUD;
import static mudgame.integration.scenarios.Scenarios.two_players;
import static testutils.Players.PLAYER_0;
import static testutils.Positions.pos;

class GameOverTest extends IntegrationTestBase {
    @Test
    void game_is_over_when_condition_is_met() {
        // given
        Scenario<?> scenario = two_players()
                .with(RectangleTerrain.land(5, 5))
                .withResources(PLAYER_0, MUD, 10);

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0,
                     createPawn(PLAYER_0, pos(0, 1)),
                     createPawn(PLAYER_0, pos(1, 0)),
                     createPawn(PLAYER_0, pos(1, 1))
                )
                .finish();
    }
}
