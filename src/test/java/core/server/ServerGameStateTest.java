package core.server;

import core.SerializationTestBase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ServerGameStateTest {


    @Nested
    class ServerGameStateSerializationTest extends SerializationTestBase {
        @Test
        void game_state_is_serializable() {
            ServerGameState gameState = ServerCore.newGameState(4);
            assertCanSerialize(gameState);
        }
    }
}