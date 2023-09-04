package mudgame.server.rules;

import core.model.PlayerID;
import core.event.Action;

import java.io.Serializable;

/**
 * Rule verifies that player can perform an action
 */
public interface ActionRule extends Serializable {
    boolean isSatisfied(Action action, PlayerID actor);
}
