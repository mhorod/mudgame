package core.entities.events;

import core.entities.model.Components;
import core.events.Event.Action;
import core.model.PlayerID;
import core.model.Position;

public record CreateEntity(Components components, PlayerID owner, Position position)
        implements Action { }
