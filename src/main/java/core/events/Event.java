package core.events;

import core.Position;
import core.entities.Entity;
import core.entities.EntityData;
import core.id.EntityID;
import core.id.PlayerID;


/**
 * Event is something that can happen and cause changes to the game
 */
public sealed interface Event
{
    /**
     * Action is an event that can be caused directly by a player
     */
    sealed interface Action extends Event { }

    record CreateEntity(EntityData entityData, PlayerID owner, Position position)
            implements Action { }

    record PlaceEntity(Entity entity, Position position) implements Event { }

    record RemoveEntity(EntityID entityID) implements Action { }

    record MoveEntity(EntityID entityID, Position destination) implements Action { }
}

