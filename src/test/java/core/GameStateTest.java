package core;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GameStateTest {


    @Nested
    class GameStateSerializationTest extends SerializationTestBase {
        @Test
        void game_state_is_serializable() {
            GameState gameState = GameCore.newGameState(4);
            assertCanSerialize(gameState);
        }
    }
}