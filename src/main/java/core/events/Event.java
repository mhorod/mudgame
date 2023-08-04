package core.events;

import core.*;

/**
 * Event is something that can happen and cause changes to the game
 */
public sealed interface Event
{
    sealed interface Action extends Event { }

    record PlaceEntity(Entity unit, PlayerID owner, Position position) implements Action { }

    record EntityPlacement(Entity entity, EntityID entityID, PlayerID owner, Position position)
            implements Event { }

    record RemoveEntity(EntityID entityID) implements Action { }

    record MoveEntity(EntityID entityID, Position position) implements Action { }
}

