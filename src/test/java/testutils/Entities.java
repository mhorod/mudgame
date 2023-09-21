package testutils;

import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.model.EntityID;
import core.model.PlayerID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Entities {
    static int nextID = 0;

    public static Entity entity(EntityData data, PlayerID owner) {
        return new Entity(data, new EntityID(nextID++), owner);
    }

    public static Entity base(PlayerID owner) {
        return new Entity(EntityData.base(), new EntityID(nextID++), owner);
    }

    public static Entity pawn(PlayerID owner) {
        return new Entity(EntityData.pawn(), new EntityID(nextID++), owner);
    }

    public static Entity warrior(PlayerID owner) {
        return new Entity(EntityData.warrior(), new EntityID(nextID++), owner);
    }

    public static Entity marshWiggle(PlayerID owner) {
        return new Entity(EntityData.marshWiggle(), new EntityID(nextID++), owner);
    }
}
