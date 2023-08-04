package core;

import java.util.Collection;

public interface EntityView
{
    record PlacedUnit(Entity entity, PlayerID owner, Position position) { }

    PlacedUnit getPlacedEntity(EntityID unitID);
    Collection<EntityID> getEntitiesAt(Position position);
    Collection<EntityID> getPlacedEntities();
}
