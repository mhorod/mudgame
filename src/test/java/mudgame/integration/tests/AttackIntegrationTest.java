package mudgame.integration.tests;

import core.entities.model.Entity;
import mudgame.controls.events.AttackEntityEvent;
import mudgame.integration.utils.RectangleTerrain;
import mudgame.integration.utils.Scenario;
import mudgame.integration.utils.ScenarioResult;
import org.junit.jupiter.api.Test;

import static mudgame.integration.assertions.ClientScenarioResultAssert.assertThatClient;
import static mudgame.integration.scenarios.Scenarios.two_players;
import static mudgame.integration.utils.Entities.pawn;
import static mudgame.integration.utils.Entities.warrior;
import static mudgame.integration.utils.Players.PLAYER_0;
import static mudgame.integration.utils.Players.PLAYER_1;
import static mudgame.integration.utils.Positions.pos;

class AttackIntegrationTest extends IntegrationTestBase {
    @Test
    void pawns_cannot_attack() {
        // given
        Entity pawn0 = pawn(PLAYER_0);
        Entity pawn1 = pawn(PLAYER_1);

        // given
        Scenario<?> scenario = two_players()
                .with(RectangleTerrain.land(1, 2))
                .with(pawn0, pos(0, 0))
                .with(pawn1, pos(0, 1));

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(pawn0, pawn1))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
    }

    @Test
    void warriors_can_attack_pawns() {
        // given
        Entity warrior = warrior(PLAYER_0);
        Entity pawn = pawn(PLAYER_1);

        // given
        Scenario<?> scenario = two_players()
                .with(RectangleTerrain.land(1, 2))
                .with(warrior, pos(0, 0))
                .with(pawn, pos(0, 1));

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior, pawn))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(AttackEntityEvent.class);
        assertThatClient(result, PLAYER_1)
                .receivedEventTypes(AttackEntityEvent.class);
    }

    @Test
    void warriors_strike_back() {
        // given
        Entity warrior0 = warrior(PLAYER_0);
        Entity warrior1 = warrior(PLAYER_1);

        // given
        Scenario<?> scenario = two_players()
                .with(RectangleTerrain.land(1, 2))
                .with(warrior0, pos(0, 0))
                .with(warrior1, pos(0, 1));

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior0, warrior1))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(AttackEntityEvent.class, AttackEntityEvent.class);
        assertThatClient(result, PLAYER_1)
                .receivedEventTypes(AttackEntityEvent.class, AttackEntityEvent.class);
    }

    @Test
    void warriors_cannot_attack_diagonally() {
        // given
        Entity warrior0 = warrior(PLAYER_0);
        Entity warrior1 = warrior(PLAYER_1);

        // given
        Scenario<?> scenario = two_players()
                .with(RectangleTerrain.land(2, 2))
                .with(warrior0, pos(0, 0))
                .with(warrior1, pos(1, 1));

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior0, warrior1))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
    }
}
