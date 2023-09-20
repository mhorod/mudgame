package mudgame.server.rules;

import mudgame.controls.actions.Action;
import core.model.PlayerID;

import java.io.Serializable;

/**
 * Rule verifies that player can perform an action
 */
public interface ActionRule extends Serializable {
    boolean isSatisfied(Action action, PlayerID actor);

    default String name() {
        return this.getClass().getSimpleName();
    }
}
