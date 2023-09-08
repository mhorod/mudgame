package mudgame.integration.utils;

import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.model.EntityID;
import core.model.PlayerID;

public class Entities {
    static int nextID = 0;

    public static Entity base(PlayerID owner) {
        return new Entity(EntityData.base(), new EntityID(nextID++), owner);
    }

    public static Entity pawn(PlayerID owner) {
        return new Entity(EntityData.pawn(), new EntityID(nextID++), owner);
    }
}
