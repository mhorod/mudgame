package core;

import core.entities.events.CreateEntity;
import core.entities.events.MoveEntity;
import core.entities.events.PlaceEntity;
import core.entities.events.RemoveEntity;
import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;

public class EntityEvents
{

    public static PlaceEntity place(Entity entity, Position position)
    {
        return new PlaceEntity(entity, position);
    }

    public static RemoveEntity remove(EntityID entityID)
    {
        return new RemoveEntity(entityID);
    }

    public static CreateEntity create(EntityData data, long playerId, Position position)
    {
        return new CreateEntity(data, new PlayerID(playerId), position);
    }

    public static MoveEntity move(EntityID entityID, Position destination)
    {
        return new MoveEntity(entityID, destination);
    }
}
