package core;

import core.events.Event.Action;
import core.model.PlayerID;

public interface ActionProcessor
{
    void process(Action action, PlayerID actor);
}
