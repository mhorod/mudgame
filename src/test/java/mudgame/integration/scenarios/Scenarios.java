package mudgame.integration.scenarios;

import mudgame.integration.utils.RectangleTerrain;
import mudgame.integration.utils.Scenario;

public class Scenarios {
    public static Scenario<?> single_player_no_base() {
        return new Scenario<>(1).with(RectangleTerrain.land(3, 3));
    }

    public static SinglePlayerWithBase single_player_with_base() {
        return new SinglePlayerWithBase()
                .with(RectangleTerrain.land(3, 3));
    }

    public static SinglePlayerWithPawn single_player_with_pawn() {
        return new SinglePlayerWithPawn()
                .with(RectangleTerrain.land(3, 3));
    }

    public static Scenario<?> two_players() {
        return new Scenario<>(2);
    }
}
