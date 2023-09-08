package mudgame.integration.scenarios;

import core.entities.model.Entity;
import mudgame.integration.utils.Scenario;

import static mudgame.integration.utils.Entities.pawn;
import static mudgame.integration.utils.Players.PLAYER_0;
import static mudgame.integration.utils.Positions.pos;

public class SinglePlayerWithPawn extends Scenario<SinglePlayerWithPawn> {
    public Entity pawn;

    public SinglePlayerWithPawn() {
        super(1);
        pawn = pawn(PLAYER_0);
        with(pawn, pos(0, 0));
    }
}
