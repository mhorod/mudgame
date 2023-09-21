package testutils.integration.scenarios;

import testutils.integration.utils.RectangleTerrain;
import testutils.integration.utils.ScenarioBuilder;

public class Scenarios {
    public static ScenarioBuilder players(int players) {
        return new ScenarioBuilder(players).with(RectangleTerrain.land(3, 3));
    }


    public static ScenarioBuilder single_player() {
        return players(1);
    }

    public static ScenarioBuilder two_players() {
        return players(2);
    }

    public static ScenarioBuilder three_players() {
        return players(3);
    }
}
