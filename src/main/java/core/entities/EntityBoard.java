package core.entities;

import core.Position;
import core.id.EntityID;
import core.id.PlayerID;

public interface EntityBoard extends EntityBoardView
{
    Entity createEntity(EntityData data, PlayerID owner, Position position);
    void placeEntity(Entity entity, Position position);
    void removeEntity(EntityID entityID);
    void moveEntity(EntityID entityID, Position targetPosition);


}
