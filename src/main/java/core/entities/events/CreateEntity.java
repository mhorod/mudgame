package core.entities.events;

import core.Position;
import core.entities.EntityData;
import core.events.Event.Action;
import core.id.PlayerID;

public record CreateEntity(EntityData entityData, PlayerID owner, Position position)
        implements Action { }
