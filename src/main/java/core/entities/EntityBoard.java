package core.entities;

import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;

public interface EntityBoard extends EntityBoardView {
    Entity createEntity(EntityData data, PlayerID owner, Position position);
    void placeEntity(Entity entity, Position position);
    void removeEntity(EntityID entityID);
    void moveEntity(EntityID entityID, Position targetPosition);

}
