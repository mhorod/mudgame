package core.entities;

import core.Position;
import core.id.EntityID;

import java.util.List;

public interface EntityBoardView
{
    List<Entity> allEntities();
    List<Entity> entitiesAt(Position position);
    Position entityPosition(EntityID entityID);
    boolean containsEntity(EntityID entityID);
}
