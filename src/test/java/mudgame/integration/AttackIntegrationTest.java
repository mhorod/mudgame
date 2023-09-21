package mudgame.integration;

import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.model.EntityID;
import testutils.integration.utils.Scenario;
import mudgame.controls.events.AttackEntityEvent;
import mudgame.controls.events.AttackPosition;
import mudgame.controls.events.DamageEntity;
import mudgame.controls.events.KillEntity;
import mudgame.controls.events.ProduceResources;
import testutils.integration.utils.RectangleTerrain;
import testutils.integration.utils.ScenarioResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.entities.model.EntityType.PAWN;
import static testutils.integration.assertions.ClientScenarioResultAssert.assertThatClient;
import static testutils.integration.assertions.IntegrationAssertions.assertIntegrity;
import static testutils.integration.assertions.IntegrationAssertions.assertNoEvents;
import static testutils.Actions.attack;
import static testutils.Entities.*;
import static testutils.Players.*;
import static testutils.Positions.pos;
import static testutils.integration.scenarios.Scenarios.*;

class AttackIntegrationTest {
    @Test
    void pawns_cannot_attack() {
        // given
        Entity pawn0 = pawn(PLAYER_0);
        Entity pawn1 = pawn(PLAYER_1);

        Scenario scenario = two_players()
                .with(pawn0, pos(0, 0))
                .with(pawn1, pos(0, 1))
                .build();

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

        Scenario scenario = two_players()
                .with(warrior, pos(0, 0))
                .with(pawn, pos(0, 1))
                .build();

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

        Scenario scenario = two_players()
                .with(warrior0, pos(0, 0))
                .with(warrior1, pos(0, 1))
                .build();

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

        Scenario scenario = two_players()
                .with(warrior0, pos(0, 0))
                .with(warrior1, pos(1, 1))
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior0, warrior1))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
    }

    @Test
    void warrior_can_kill_pawn() {
        // given
        Entity warrior = warrior(PLAYER_0);
        Entity pawn = pawn(PLAYER_1);
        pawn.damage(7); // Leave 1HP

        // given
        Scenario scenario = two_players()
                .with(warrior, pos(0, 0))
                .with(pawn, pos(0, 1))
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior, pawn))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(AttackEntityEvent.class,
                                    KillEntity.class,
                                    ProduceResources.class);
        assertThatClient(result, PLAYER_1)
                .receivedEventTypes(AttackEntityEvent.class, KillEntity.class);
    }

    @Test
    void entity_does_not_strike_back_when_killed() {
        // given
        Entity warrior0 = warrior(PLAYER_0);
        Entity warrior1 = warrior(PLAYER_1);
        warrior1.damage(11); // Leave 1 HP

        Scenario scenario = two_players()
                .with(warrior0, pos(0, 0))
                .with(warrior1, pos(0, 1))
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior0, warrior1))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_0)
                .receivedEventTypes(
                        AttackEntityEvent.class,
                        KillEntity.class,
                        ProduceResources.class
                );
        assertThatClient(result, PLAYER_1)
                .receivedEventTypes(AttackEntityEvent.class, KillEntity.class);
    }

    @Test
    void player_does_not_receive_attack_events_when_attack_is_hidden() {
        // given
        Entity warrior0 = warrior(PLAYER_0);
        Entity warrior1 = warrior(PLAYER_1);
        warrior1.damage(11); // Leave 1 HP

        Scenario scenario = three_players()
                .with(warrior0, pos(0, 0))
                .with(warrior1, pos(0, 1))
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior0, warrior1))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_2).receivedNoEvents();
    }

    @Test
    void player_receives_attack_position_when_does_not_see_attacked_entity_but_sees_attacker() {
        // given
        Entity warrior0 = warrior(PLAYER_0);
        Entity warrior1 = warrior(PLAYER_1);
        Entity pawn = pawn(PLAYER_2);
        warrior1.damage(11); // Leave 1 HP

        Scenario scenario = three_players()
                .with(warrior0, pos(0, 2))
                .with(warrior1, pos(0, 3))
                .with(pawn, pos(0, 0))
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior0, warrior1))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_2).receivedEventTypes(AttackPosition.class);
    }

    @Test
    void player_receives_damage_entity_and_kill_when_does_not_see_attacker_but_sees_attacked_entity() {
        // given
        Entity warrior0 = warrior(PLAYER_0);
        Entity warrior1 = warrior(PLAYER_1);
        Entity pawn = pawn(PLAYER_2);
        warrior1.damage(11); // Leave 1 HP

        Scenario scenario = three_players()
                .with(RectangleTerrain.land(1, 6))
                .with(warrior0, pos(0, 2))
                .with(warrior1, pos(0, 3))
                .with(pawn, pos(0, 5))
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior0, warrior1))
                .finish();

        // then
        assertIntegrity(result);
        assertThatClient(result, PLAYER_2).receivedEventTypes(DamageEntity.class, KillEntity.class);
    }

    @Test
    void cannot_attack_entity_with_no_health_component() {
        // given
        Entity warrior = warrior(PLAYER_0);
        Entity rock = entity(new EntityData(PAWN, List.of()), PLAYER_1);

        Scenario scenario = two_players()
                .with(warrior, pos(0, 0))
                .with(rock, pos(0, 1))
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior, rock))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
    }

    @Test
    void player_cannot_attack_own_entities() {
        // given
        Entity warrior = warrior(PLAYER_0);
        Entity pawn = pawn(PLAYER_0);

        Scenario scenario = single_player()
                .with(warrior, pos(0, 0))
                .with(pawn, pos(0, 1))
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior, pawn))
                .finish();

        assertIntegrity(result);
        assertNoEvents(result);
    }

    @Test
    void cannot_attack_with_unowned_entity() {
        // given
        Entity warrior0 = warrior(PLAYER_1);
        Entity warrior1 = pawn(PLAYER_2);
        Scenario scenario = three_players()
                .with(warrior0, pos(0, 0))
                .with(warrior1, pos(0, 1))
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior0, warrior1))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
    }

    @Test
    void cannot_attack_with_non_existent_entity() {
        // given
        Entity warrior = warrior(PLAYER_1);
        Scenario scenario = two_players()
                .with(warrior, pos(0, 0))
                .build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(new EntityID(1), warrior.id()))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
    }

    @Test
    void cannot_attack_non_existent_entity() {
        // given
        Entity warrior = warrior(PLAYER_0);
        Scenario scenario = two_players().with(warrior, pos(0, 0)).build();

        // when
        ScenarioResult result = scenario
                .act(PLAYER_0, attack(warrior.id(), new EntityID(1)))
                .finish();

        // then
        assertIntegrity(result);
        assertNoEvents(result);
    }
}
