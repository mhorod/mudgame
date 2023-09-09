package mudgame.integration.tests;

import core.entities.model.Entity;
import core.event.Action;
import core.model.PlayerID;
import core.model.Position;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.actions.MoveEntity;
import mudgame.integration.utils.ScenarioResult;

import static core.entities.EntityBoardAssert.assertThatEntityBoard;
import static core.entities.model.EntityType.PAWN;
import static org.assertj.core.api.Assertions.assertThat;

abstract class IntegrationTestBase {


    protected void assertIntegrity(ScenarioResult result) {
        for (PlayerID player : result.players()) {
            System.out.println("Asserting integrity for player: " + player);
            assertThat(result.serverFow(player)).isEqualTo(result.clientFow(player));
            assertThatEntityBoard(result.serverEntityBoard(player))
                    .isEqualTo(result.clientEntityBoard(player));
            assertThat(result.serverTurn()).isEqualTo(result.clientTurn(player));
        }
    }


    protected void assertNoEvents(ScenarioResult result) {
        assertThat(result.receivedEvents()).allSatisfy((p, es) -> assertThat(es).isEmpty());
    }

    protected Action move(Entity entity, Position position) {
        return new MoveEntity(entity.id(), position);
    }

    protected Action createPawn(PlayerID player, Position position) {
        return new CreateEntity(PAWN, player, position);
    }

    protected Action attack(Entity attacker, Entity attacked) {
        return new AttackEntityAction(attacker.id(), attacked.id());
    }

}
