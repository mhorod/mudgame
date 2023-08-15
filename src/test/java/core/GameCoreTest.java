package core;

import core.events.Event.Action;
import core.events.EventSender;
import core.id.PlayerID;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.GameCoreTest.GameCoreAssert.assertThat;

class GameCoreTest
{
    static class GameCoreAssert extends AbstractAssert<GameCoreAssert, GameCore>
    {
        protected GameCoreAssert(GameCore actual)
        {
            super(actual, GameCoreAssert.class);
        }

        GameCoreAssert isInEqualStateAs(GameCore expected)
        {
            if (!actual.entityBoard.equals(expected.entityBoard))
                failWithMessage("Entity boards differ");
            else if (!actual.fogOfWar.equals(expected.fogOfWar))
                failWithMessage("Fogs of war differ");
            return this;
        }

        static GameCoreAssert assertThat(GameCore actual)
        {
            return new GameCoreAssert(actual);
        }
    }

    @Nested
    class ServerClientConsistencyTest
    {

        record PlayerAction(Action action, PlayerID actor) { }

        void testScenario(int playerCount, List<PlayerAction> playerActions)
        {
            EventSender serverEventSender = new EventSender();
            EventSender clientEventSender = new EventSender();

            GameCore server = new GameCore(playerCount, serverEventSender);
            GameCore client = new GameCore(playerCount, clientEventSender);
            serverEventSender.addObserver(client);

            for (PlayerAction playerAction : playerActions)
                server.process(playerAction.action(), playerAction.actor());

            assertThat(client).isInEqualStateAs(server);
        }

        @Test
        void test_no_actions_scenario()
        {
            testScenario(2, List.of());
        }
    }

}