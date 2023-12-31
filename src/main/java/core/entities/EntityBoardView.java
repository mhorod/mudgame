package core.entities;

import core.entities.model.Entity;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;

import java.util.List;

public interface EntityBoardView {
    List<Entity> allEntities();
    List<Entity> entitiesAt(Position position);
    Position entityPosition(EntityID entityID);
    boolean containsEntity(EntityID entityID);
    PlayerID entityOwner(EntityID entityID);
    Entity findEntityByID(EntityID entityID);
    List<Entity> playerEntities(PlayerID currentPlayer);
}
