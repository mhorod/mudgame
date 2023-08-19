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

public class EntityActions
{

    public static PlayerAction<PlaceEntity> place(long actor, Entity entity, Position position)
    {
        return PlayerAction.from(actor, new PlaceEntity(entity, position));
    }

    public static PlayerAction<RemoveEntity> remove(long actor, EntityID entityID)
    {
        return PlayerAction.from(actor, new RemoveEntity(entityID));
    }

    public static PlayerAction<CreateEntity> create(
            long actor,
            EntityData data, long playerId, Position position
    )
    {
        return PlayerAction.from(actor, new CreateEntity(data, new PlayerID(playerId), position));
    }

    public static PlayerAction<MoveEntity> move(long actor, EntityID entityID, Position destination)
    {
        return PlayerAction.from(actor, new MoveEntity(entityID, destination));
    }
}
