package mudgame.integration.tests;

import mudgame.controls.events.SpawnEntity;
import mudgame.integration.scenarios.SinglePlayerWithBase;
import mudgame.integration.utils.RectangleTerrain;
import mudgame.integration.utils.Scenario;
import mudgame.integration.utils.ScenarioResult;
import org.junit.jupiter.api.Test;

import static core.entities.model.EntityType.PAWN;
import static mudgame.integration.assertions.ClientScenarioResultAssert.assertThatClient;
import static mudgame.integration.scenarios.Scenarios.*;
import static testutils.Entities.base;
import static testutils.Entities.pawn;
import static testutils.Players.PLAYER_0;
import static testutils.Players.PLAYER_1;
import static testutils.Positions.pos;

class CreationIntegrationTest extends IntegrationTestBase {
    @Test
    void no_actions_are_performed_when_player_has_no_entities() {
        // given
        Scenario<?> scenario = single_player_no_base();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, createPawn(PLAYER_0, pos(0, 0)))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
    }

    @Test
    void player_can_create_pawn_near_base_on_claimed_position() {
        // given
        SinglePlayerWithBase scenario = single_player_with_base();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, createPawn(PLAYER_0, pos(0, 1)))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(SpawnEntity.class)
                .cannotCreateEntityOn(PAWN, pos(0, 1));
    }

    @Test
    void pawn_does_not_claim() {
        // given
        Scenario<?> scenario = single_player()
                .with(pawn(PLAYER_0), pos(0, 0));

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, createPawn(PLAYER_0, pos(0, 1)))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
        assertThatClient(result, PLAYER_0).cannotCreate(PAWN);
    }

    @Test
    void creating_marsh_wiggle_claims_area() {
        Scenario<?> scenario = single_player()
                .with(RectangleTerrain.land(5, 5))
                .with(base(PLAYER_0), pos(0, 0));

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, createMarshWiggle(PLAYER_0, pos(2, 2)))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0).receivedEventTypes(SpawnEntity.class);
        assertThatClient(result, PLAYER_0).owns(pos(3, 3));
    }

    @Test
    void claim_is_not_overridden_when_creating_entity() {
        // given
        Scenario<?> scenario = two_players()
                .with(RectangleTerrain.land(6, 6))
                .with(base(PLAYER_0), pos(0, 0))
                .with(base(PLAYER_1), pos(5, 5));

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, createMarshWiggle(PLAYER_0, pos(3, 3)))
                .finish();

        assertIntegrity(result);
        assertThatClient(result, PLAYER_1).owns(pos(3, 3));
        assertThatClient(result, PLAYER_0).doesNotOwn(pos(3, 3));
    }
}
