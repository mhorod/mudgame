package core.turns;

import core.PlayerAction;
import lombok.experimental.UtilityClass;
import mudgame.controls.actions.CompleteTurn;

@UtilityClass
public class TurnEvents {
    public static CompleteTurn completeTurn() { return new CompleteTurn(); }

    public static PlayerAction<CompleteTurn> completeTurn(long actor) {
        return PlayerAction.from(actor, completeTurn());
    }
}
