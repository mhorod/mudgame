package core.entities.events;

import core.model.Position;
import core.entities.model.EntityData;
import core.events.Event.Action;
import core.model.PlayerID;

public record CreateEntity(EntityData entityData, PlayerID owner, Position position)
        implements Action { }
