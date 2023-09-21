package mudgame.integration;

import testutils.integration.utils.RectangleTerrain;
import testutils.integration.utils.Scenario;
import testutils.integration.utils.ScenarioResult;
import org.junit.jupiter.api.Test;

import static core.resources.ResourceType.MUD;
import static testutils.integration.scenarios.Scenarios.two_players;
import static testutils.Actions.createPawn;
import static testutils.Players.PLAYER_0;
import static testutils.Positions.pos;

class GameOverTest {
    @Test
    void game_is_over_when_condition_is_met() {
        // given
        Scenario scenario = two_players()
                .with(RectangleTerrain.land(5, 5))
                .withResources(PLAYER_0, MUD, 10)
                .build();

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
