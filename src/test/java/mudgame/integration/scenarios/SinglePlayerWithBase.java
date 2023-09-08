package mudgame.integration.scenarios;

import core.entities.model.Entity;
import mudgame.integration.utils.Scenario;

import static mudgame.integration.utils.Entities.base;
import static mudgame.integration.utils.Players.PLAYER_0;
import static mudgame.integration.utils.Positions.pos;

public class SinglePlayerWithBase extends Scenario<SinglePlayerWithBase> {
    public Entity base;

    public SinglePlayerWithBase() {
        super(1);
        base = base(PLAYER_0);
        with(base, pos(0, 0));
    }
}
