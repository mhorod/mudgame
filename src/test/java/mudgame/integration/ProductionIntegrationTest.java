package mudgame.integration;

import testutils.integration.utils.Scenario;
import mudgame.controls.events.NextTurn;
import mudgame.controls.events.ProduceResources;
import testutils.integration.utils.ScenarioResult;
import org.junit.jupiter.api.Test;

import static core.resources.ResourceType.MUD;
import static testutils.integration.assertions.ClientScenarioResultAssert.assertThatClient;
import static testutils.integration.assertions.IntegrationAssertions.assertIntegrity;
import static testutils.Actions.completeTurn;
import static testutils.Entities.base;
import static testutils.Players.PLAYER_0;
import static testutils.Players.PLAYER_1;
import static testutils.Positions.pos;
import static testutils.integration.scenarios.Scenarios.single_player;
import static testutils.integration.scenarios.Scenarios.two_players;

class ProductionIntegrationTest {
    @Test
    void base_produces_resources_when_new_turn_starts() {
        // given
        Scenario scenario = single_player()
                .with(base(PLAYER_0), pos(0, 0))
                .build();

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
        Scenario scenario = two_players()
                .with(base(PLAYER_0), pos(0, 0))
                .with(base(PLAYER_1), pos(1, 1))
                .build();

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
        Scenario scenario = two_players()
                .with(base(PLAYER_0), pos(0, 0))
                .with(base(PLAYER_1), pos(1, 1))
                .build();

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
