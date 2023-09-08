package mudgame.integration.scenarios;

import core.entities.model.Entity;
import core.model.Position;
import mudgame.integration.utils.Scenario;

import static mudgame.integration.utils.Entities.base;
import static mudgame.integration.utils.Players.PLAYER_0;
import static mudgame.integration.utils.Players.PLAYER_1;

public class TwoPlayersWithBases extends Scenario<TwoPlayersWithBases> {
    public Entity base0;
    public Entity base1;

    public TwoPlayersWithBases(Position p0, Position p1) {
        super(2);
        base0 = base(PLAYER_0);
        base1 = base(PLAYER_1);
        with(base0, p0);
        with(base1, p1);
    }
}
