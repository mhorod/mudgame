package core.rules;

import core.events.Event.Action;
import core.id.PlayerID;

/**
 * Rule verifies that player can perform an action
 */
public interface ActionRule
{
    boolean isSatisfied(Action action, PlayerID actor);
}
