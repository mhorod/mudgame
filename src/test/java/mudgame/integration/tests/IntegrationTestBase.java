package mudgame.integration.tests;

import core.entities.model.Entity;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import mudgame.controls.actions.Action;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.actions.CompleteTurn;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.actions.MoveEntity;
import mudgame.integration.utils.ScenarioResult;

import static core.claiming.PlayerClaimedAreaAssert.assertThatPlayerClaimedArea;
import static core.entities.EntityBoardAssert.assertThatEntityBoard;
import static core.entities.model.EntityType.MARSH_WIGGLE;
import static core.entities.model.EntityType.PAWN;
import static core.fogofwar.PlayerFogOfWarAssert.assertThatPlayerFow;
import static org.assertj.core.api.Assertions.assertThat;

abstract class IntegrationTestBase {


    protected void assertIntegrity(ScenarioResult result) {
        for (PlayerID player : result.players()) {
            System.out.println("Asserting integrity for player: " + player);
            assertTurnIntegrity(result, player);
            assertFowIntegrity(result, player);
            assertEntityBoardIntegrity(result, player);
            assertClaimedAreaIntegrity(result, player);
            assertResourcesIntegrity(result, player);
            assertGameOverIntegrity(result, player);
        }
    }

    private void assertGameOverIntegrity(ScenarioResult result, PlayerID player) {
        assertThat(result.clientIsGameOver(player)).isEqualTo(result.serverIsGameOver());
        assertThat(result.clientWinners(player)).isEqualTo(result.serverWinners());
    }

    private void assertResourcesIntegrity(ScenarioResult result, PlayerID player) {
        assertThat(result.clientResources(player)).isEqualTo(result.serverResources(player));
    }

    private void assertTurnIntegrity(ScenarioResult result, PlayerID player) {
        assertThat(result.clientTurn(player)).isEqualTo(result.serverTurn());
    }

    private void assertClaimedAreaIntegrity(ScenarioResult result, PlayerID player) {
        assertThatPlayerClaimedArea(result.clientClaimedArea(player)).isEqualTo(
                result.serverClaimedArea(player));
    }

    private void assertEntityBoardIntegrity(ScenarioResult result, PlayerID player) {
        assertThatEntityBoard(result.clientEntityBoard(player)).isEqualTo(
                result.serverEntityBoard(player));
    }

    private void assertFowIntegrity(ScenarioResult result, PlayerID player) {
        assertThatPlayerFow(result.clientFow(player)).isEqualTo(result.serverFow(player));
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

    protected Action createMarshWiggle(PlayerID player, Position position) {
        return new CreateEntity(MARSH_WIGGLE, player, position);
    }

    protected Action attack(EntityID attacker, EntityID attacked) {
        return new AttackEntityAction(attacker, attacked);
    }

    protected Action attack(Entity attacker, Entity attacked) {
        return new AttackEntityAction(attacker.id(), attacked.id());
    }

    protected Action completeTurn() {
        return new CompleteTurn();
    }

}
