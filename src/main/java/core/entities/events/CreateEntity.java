package core.entities.events;

import core.entities.model.EntityData;
import core.events.model.Event.Action;
import core.model.PlayerID;
import core.model.Position;

public record CreateEntity(EntityData entityData, PlayerID owner, Position position)
        implements Action { }
