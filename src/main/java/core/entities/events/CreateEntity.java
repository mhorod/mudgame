package core.entities.events;

import core.entities.components.Component;
import core.events.Event.Action;
import core.model.PlayerID;
import core.model.Position;

import java.util.List;

public record CreateEntity(List<Component> components, PlayerID owner, Position position)
        implements Action { }
