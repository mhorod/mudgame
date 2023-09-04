package core.entities.events;

import core.entities.model.EntityType;
import mudgame.events.Event.Action;
import core.model.PlayerID;
import core.model.Position;

public record CreateEntity(EntityType type, PlayerID owner, Position position)
        implements Action { }
