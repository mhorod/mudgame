package mudgame.integration;

import testutils.integration.utils.Scenario;
import mudgame.controls.events.ChargeResources;
import mudgame.controls.events.SpawnEntity;
import testutils.integration.utils.RectangleTerrain;
import testutils.integration.utils.ScenarioResult;
import org.junit.jupiter.api.Test;

import static core.entities.model.EntityType.PAWN;
import static core.resources.ResourceType.MUD;
import static testutils.integration.assertions.ClientScenarioResultAssert.assertThatClient;
import static testutils.integration.assertions.IntegrationAssertions.assertIntegrity;
import static testutils.integration.assertions.IntegrationAssertions.assertNoEvents;
import static testutils.Actions.createMarshWiggle;
import static testutils.Actions.createPawn;
import static testutils.Entities.base;
import static testutils.Entities.pawn;
import static testutils.Players.PLAYER_0;
import static testutils.Players.PLAYER_1;
import static testutils.Positions.pos;
import static testutils.integration.scenarios.Scenarios.single_player;
import static testutils.integration.scenarios.Scenarios.two_players;

class CreationIntegrationTest {
    @Test
    void no_actions_are_performed_when_player_has_no_entities() {
        // given
        Scenario scenario = single_player().build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, createPawn(PLAYER_0, pos(0, 0)))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
    }

    @Test
    void player_cannot_create_entity_when_has_no_resources() {
        // given
        Scenario scenario = single_player()
                .with(RectangleTerrain.land(3, 3))
                .with(base(PLAYER_0), pos(0, 0))
                .withResources(PLAYER_0, MUD, 0)
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, createPawn(PLAYER_0, pos(0, 1)))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
    }

    @Test
    void player_can_create_pawn_near_base_on_claimed_position_when_has_enough_resources() {
        // given
        Scenario scenario = single_player()
                .with(RectangleTerrain.land(3, 3))
                .with(base(PLAYER_0), pos(0, 0))
                .withResources(PLAYER_0, MUD, 10)
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, createPawn(PLAYER_0, pos(0, 1)))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(SpawnEntity.class, ChargeResources.class)
                .cannotCreateEntityOn(PAWN, pos(0, 1));
    }

    @Test
    void creating_entity_uses_resources() {
        // given
        Scenario scenario = two_players()
                .with(base(PLAYER_0), pos(0, 0))
                .with(base(PLAYER_1), pos(0, 2))
                .withResources(PLAYER_0, MUD, 1)
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, createPawn(PLAYER_0, pos(0, 1)))
                .act(PLAYER_0, createPawn(PLAYER_0, pos(1, 1)))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(SpawnEntity.class, ChargeResources.class)
                .has(0, MUD);
        assertThatClient(result, PLAYER_1)
                .receivedEventTypes(SpawnEntity.class);

    }

    @Test
    void pawn_does_not_claim() {
        // given
        Scenario scenario = single_player()
                .with(pawn(PLAYER_0), pos(0, 0))
                .build();

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
        Scenario scenario = single_player()
                .withResources(PLAYER_0, MUD, 10)
                .with(RectangleTerrain.land(5, 5))
                .with(base(PLAYER_0), pos(0, 0))
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, createMarshWiggle(PLAYER_0, pos(2, 2)))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(SpawnEntity.class, ChargeResources.class);
        assertThatClient(result, PLAYER_0).owns(pos(3, 3));
    }

    @Test
    void claim_is_not_overridden_when_creating_entity() {
        // given
        Scenario scenario = two_players()
                .with(RectangleTerrain.land(6, 6))
                .with(base(PLAYER_0), pos(0, 0))
                .with(base(PLAYER_1), pos(5, 5))
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, createMarshWiggle(PLAYER_0, pos(3, 3)))
                .finish();

        assertIntegrity(result);
        assertThatClient(result, PLAYER_1).owns(pos(3, 3));
        assertThatClient(result, PLAYER_0).doesNotOwn(pos(3, 3));
    }
}
