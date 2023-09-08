package mudgame.integration;

import core.entities.model.Entity;
import core.event.Action;
import core.event.Event;
import core.model.PlayerID;
import core.model.Position;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.actions.MoveEntity;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.PlaceEntity;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.SpawnEntity;
import mudgame.integration.scenarios.SinglePlayerWithBase;
import mudgame.integration.scenarios.SinglePlayerWithPawn;
import mudgame.integration.utils.RectangleTerrain;
import mudgame.integration.utils.Scenario;
import mudgame.integration.utils.ScenarioResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.entities.EntityBoardAssert.assertThatEntityBoard;
import static core.entities.model.EntityType.PAWN;
import static mudgame.integration.assertions.ClientScenarioResultAssert.assertThatClient;
import static mudgame.integration.scenarios.Scenarios.*;
import static mudgame.integration.utils.Entities.pawn;
import static mudgame.integration.utils.Players.PLAYER_0;
import static mudgame.integration.utils.Players.PLAYER_1;
import static mudgame.integration.utils.Positions.pos;
import static org.assertj.core.api.Assertions.assertThat;

class IntegrationTest {

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
    void base_cannot_be_moved() {
        // given
        SinglePlayerWithBase scenario = single_player_with_base();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, moveEntity(scenario.base, pos(0, 1)))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
    }

    @Test
    void player_can_create_pawn_near_base() {
        // given
        SinglePlayerWithBase scenario = single_player_with_base();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, createPawn(PLAYER_0, pos(0, 1)))
                .finish();

        // then
        assertIntegrity(result);
        assertEventTypes(result.clientEvents(PLAYER_0), SpawnEntity.class);
    }

    @Test
    void pawn_can_be_moved() {
        // given
        SinglePlayerWithPawn scenario = single_player_with_pawn();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, moveEntity(scenario.pawn, pos(1, 1)))
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
                .act(PLAYER_0, moveEntity(pawn0, pos(2, 2)))
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
                .act(PLAYER_0, moveEntity(pawn0, pos(0, 0)))
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

    private void assertIntegrity(ScenarioResult result) {
        for (PlayerID player : result.players()) {
            System.err.println("Asserting integrity for player: " + player);
            assertThat(result.serverFow(player)).isEqualTo(result.clientFow(player));
            assertThatEntityBoard(result.serverEntityBoard(player))
                    .isEqualTo(result.clientEntityBoard(player));
            assertThat(result.serverTurn()).isEqualTo(result.clientTurn(player));
        }
    }


    @SafeVarargs
    private void assertEventTypes(List<Event> actual, Class<? extends Event>... expected) {
        assertThat(actual).hasSize(expected.length);
        for (int i = 0; i < actual.size(); i++)
            assertThat(actual.get(i)).isInstanceOf(expected[i]);
    }

    private void assertNoEvents(ScenarioResult result) {
        assertThat(result.receivedEvents()).allSatisfy((p, es) -> assertThat(es).isEmpty());
    }

    private Action moveEntity(Entity entity, Position position) {
        return new MoveEntity(entity.id(), position);
    }

    private Action createPawn(PlayerID player, Position position) {
        return new CreateEntity(PAWN, player, position);
    }

}
