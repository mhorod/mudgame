package mudgame.client;

import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.model.EntityID;
import core.turns.TurnManager;
import core.turns.TurnView;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static testutils.Entities.pawn;
import static testutils.Entities.warrior;
import static testutils.Players.PLAYER_0;
import static testutils.Players.PLAYER_1;
import static testutils.Positions.pos;

class PlayerAttackManagerTest {

    private final EntityBoard entityBoard = new EntityBoard();
    private final TurnView turnView = new TurnManager(2);
    private final PlayerAttackManager testee = new PlayerAttackManager(PLAYER_0, entityBoard, turnView);

    @Test
    void pawn_cannot_attack() {
        // given
        Entity pawn = pawn(PLAYER_0);
        entityBoard.placeEntity(pawn, pos(0, 0));
        entityBoard.placeEntity(pawn(PLAYER_1), pos(0, 1));

        // when
        List<EntityID> result = testee.attackableEntities(pawn.id());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void warrior_can_attack_enemy() {
        // given
        Entity warrior = warrior(PLAYER_0);
        Entity pawn = pawn(PLAYER_1);
        entityBoard.placeEntity(warrior, pos(0, 0));
        entityBoard.placeEntity(pawn, pos(0, 1));

        // when
        List<EntityID> result = testee.attackableEntities(warrior.id());

        // then
        assertThat(result).containsExactly(pawn.id());
    }

    @Test
    void warrior_cannot_attack_own_pawn() {
        // given
        Entity warrior = warrior(PLAYER_0);
        Entity pawn = pawn(PLAYER_0);
        entityBoard.placeEntity(warrior, pos(0, 0));
        entityBoard.placeEntity(pawn, pos(0, 1));

        // when
        List<EntityID> result = testee.attackableEntities(warrior.id());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void warrior_cannot_attack_two_tiles_away() {
        // given
        Entity warrior = warrior(PLAYER_0);
        Entity pawn = pawn(PLAYER_1);
        entityBoard.placeEntity(warrior, pos(0, 0));
        entityBoard.placeEntity(pawn, pos(0, 2));

        // when
        List<EntityID> result = testee.attackableEntities(warrior.id());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void warrior_cannot_attack_diagonally() {
        // given
        Entity warrior = warrior(PLAYER_0);
        Entity pawn = pawn(PLAYER_1);
        entityBoard.placeEntity(warrior, pos(0, 0));
        entityBoard.placeEntity(pawn, pos(1, 1));

        // when
        List<EntityID> result = testee.attackableEntities(warrior.id());

        // then
        assertThat(result).isEmpty();
    }
}