package testutils.integration.assertions;

import core.model.PlayerID;
import testutils.integration.utils.ScenarioResult;
import lombok.experimental.UtilityClass;

import static core.claiming.PlayerClaimedAreaAssert.assertThatPlayerClaimedArea;
import static core.entities.EntityBoardAssert.assertThatEntityBoard;
import static core.fogofwar.PlayerFogOfWarAssert.assertThatPlayerFow;
import static org.assertj.core.api.Assertions.assertThat;

@UtilityClass
public class IntegrationAssertions {

    public static void assertIntegrity(ScenarioResult result) {
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


    public static void assertNoEvents(ScenarioResult result) {
        assertThat(result.receivedEvents()).allSatisfy((p, es) -> assertThat(es).isEmpty());
    }

}
