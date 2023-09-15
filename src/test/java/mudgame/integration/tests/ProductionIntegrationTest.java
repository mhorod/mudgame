package mudgame.integration.tests;

import mudgame.controls.events.NextTurn;
import mudgame.controls.events.ProduceResources;
import mudgame.integration.scenarios.SinglePlayerWithBase;
import mudgame.integration.utils.Scenario;
import mudgame.integration.utils.ScenarioResult;
import org.junit.jupiter.api.Test;

import static core.resources.ResourceType.MUD;
import static mudgame.integration.assertions.ClientScenarioResultAssert.assertThatClient;
import static mudgame.integration.scenarios.Scenarios.single_player_with_base;
import static mudgame.integration.scenarios.Scenarios.two_players;
import static testutils.Entities.base;
import static testutils.Players.PLAYER_0;
import static testutils.Players.PLAYER_1;
import static testutils.Positions.pos;

class ProductionIntegrationTest extends IntegrationTestBase {
    @Test
    void base_produces_resources_when_new_turn_starts() {
        // given
        SinglePlayerWithBase scenario = single_player_with_base();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, completeTurn())
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(NextTurn.class, ProduceResources.class)
                .has(2, MUD);
    }

    @Test
    void player_does_not_produce_resources_in_first_turn() {
        // given
        Scenario<?> scenario = two_players()
                .with(base(PLAYER_0), pos(0, 0))
                .with(base(PLAYER_1), pos(1, 1));

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, completeTurn())
                .finish();
        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(NextTurn.class)
                .has(0, MUD);
        assertThatClient(result, PLAYER_1)
                .receivedEventTypes(NextTurn.class)
                .has(0, MUD);
    }

    @Test
    void player_produces_resources_in_second_turn() {
        // given
        Scenario<?> scenario = two_players()
                .with(base(PLAYER_0), pos(0, 0))
                .with(base(PLAYER_1), pos(1, 1));

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, completeTurn())
                .act(PLAYER_1, completeTurn())
                .finish();
        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(NextTurn.class, NextTurn.class, ProduceResources.class)
                .has(2, MUD);
        assertThatClient(result, PLAYER_1)
                .receivedEventTypes(NextTurn.class, NextTurn.class)
                .has(0, MUD);
    }
}
