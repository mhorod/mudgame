package mudgame.server;

import core.SerializationTestBase;
import mudgame.server.state.ServerState;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ServerStateTest {


    @Nested
    class ServerStateSerializationTest extends SerializationTestBase {
        @Test
        void game_state_is_serializable() {
            ServerState gameState = new MudServerCore(4).state();
            assertCanSerialize(gameState);
        }
    }
}