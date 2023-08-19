package core;


import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.model.EntityID;
import core.model.PlayerID;

import static org.mockito.Mockito.mock;

class GameCoreTest
{


    static Entity mockEntity(long entityID, long playerID)
    {
        return new Entity(mock(EntityData.class), new EntityID(entityID), new PlayerID(playerID));
    }


}