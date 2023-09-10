package mudgame.server;

import core.SerializationTestBase;
import core.model.PlayerID;
import mudgame.client.ClientGameState;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ServerGameStateTest {


    @Nested
    class ServerGameStateSerializationTest extends SerializationTestBase {
        @Test
        void game_state_is_serializable() {
            ServerGameState gameState = new MudServerCore(4).state();
            assertCanSerialize(gameState);
        }

        @Test
        void client_game_state_is_serializable() {
            ClientGameState gameState = new MudServerCore(4).state().toClientGameState(new PlayerID(0));
            assertCanSerialize(gameState);
        }
    }
}
