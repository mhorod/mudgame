package mudgame.integration.scenarios;

import mudgame.integration.utils.RectangleTerrain;
import mudgame.integration.utils.Scenario;

public class Scenarios {
    public static Scenario<?> players(int players) {
        return new Scenario<>(players).with(RectangleTerrain.land(3, 3));
    }

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

    public static Scenario<?> single_player() {
        return players(1);
    }

    public static Scenario<?> two_players() {
        return players(2);
    }

    public static Scenario<?> three_players() {
        return players(3);
    }
}
