package mudgame.server.rules;

import mudgame.events.Event.Action;
import core.model.PlayerID;

import java.io.Serializable;

/**
 * Rule verifies that player can perform an action
 */
public interface ActionRule extends Serializable {
    boolean isSatisfied(Action action, PlayerID actor);
}
