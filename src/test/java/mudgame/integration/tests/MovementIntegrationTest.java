package mudgame.integration.tests;

import core.entities.model.Entity;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.PlaceEntity;
import mudgame.controls.events.RemoveEntity;
import mudgame.integration.scenarios.SinglePlayerWithBase;
import mudgame.integration.scenarios.SinglePlayerWithPawn;
import mudgame.integration.utils.RectangleTerrain;
import mudgame.integration.utils.Scenario;
import mudgame.integration.utils.ScenarioResult;
import org.junit.jupiter.api.Test;

import static mudgame.integration.assertions.ClientScenarioResultAssert.assertThatClient;
import static mudgame.integration.scenarios.Scenarios.*;
import static testutils.Entities.*;
import static testutils.Players.PLAYER_0;
import static testutils.Players.PLAYER_1;
import static testutils.Positions.pos;

class MovementIntegrationTest extends IntegrationTestBase {
    @Test
    void base_cannot_be_moved() {
        // given
        SinglePlayerWithBase scenario = single_player_with_base();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, move(scenario.base, pos(0, 1)))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
    }


    @Test
    void pawn_can_be_moved() {
        // given
        SinglePlayerWithPawn scenario = single_player_with_pawn();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, move(scenario.pawn, pos(1, 1)))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0).receivedEventTypes(MoveEntityAlongPath.class);
    }

    @Test
    void moving_pawn_shows_other_entities_and_is_seen_by_them() {
        Entity pawn0 = pawn(PLAYER_0);
        Entity pawn1 = pawn(PLAYER_1);

        // given
        Scenario<?> scenario = two_players()
                .with(RectangleTerrain.land(5, 5))
                .with(pawn0, pos(0, 0))
                .with(pawn1, pos(4, 4));

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, move(pawn0, pos(2, 2)))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(MoveEntityAlongPath.class)
                .sees(pos(4, 4))
                .seesEntities(pawn1);

        assertThatClient(result, PLAYER_1)
                .receivedEventTypes(PlaceEntity.class, MoveEntityAlongPath.class)
                .seesEntities(pawn0);
    }

    @Test
    void moving_pawn_away_hides_other_entities_and_is_not_seen_by_them() {
        Entity pawn0 = pawn(PLAYER_0);
        Entity pawn1 = pawn(PLAYER_1);

        // given
        Scenario<?> scenario = two_players()
                .with(RectangleTerrain.land(5, 5))
                .with(pawn0, pos(2, 2))
                .with(pawn1, pos(4, 4));

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, move(pawn0, pos(0, 0)))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(MoveEntityAlongPath.class)
                .doesNotSee(pos(4, 4))
                .doesNotSeeEntities(pawn1);

        assertThatClient(result, PLAYER_1)
                .receivedEventTypes(MoveEntityAlongPath.class, RemoveEntity.class)
                .doesNotSeeEntities(pawn0);
    }

    @Test
    void moving_marsh_wiggle_changes_claimed_area() {
        Entity marshWiggle = marshWiggle(PLAYER_0);
        Scenario<?> scenario = single_player()
                .with(RectangleTerrain.land(6, 6))
                .with(base(PLAYER_0), pos(0, 0))
                .with(marshWiggle, pos(0, 1));

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, move(marshWiggle, pos(3, 3)))
                .act(PLAYER_0, move(marshWiggle, pos(5, 5)))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(MoveEntityAlongPath.class, MoveEntityAlongPath.class);
        assertThatClient(result, PLAYER_0)
                .owns(pos(4, 4))
                .doesNotOwn(pos(3, 3));
    }
}