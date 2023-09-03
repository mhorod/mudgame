package core.entities.model;

import core.entities.components.Component;
import core.model.EntityID;
import core.model.PlayerID;

import java.io.Serializable;
import java.util.List;

/**
 * Represents an existing, individual entity instance that has id and owner
 */
public record Entity(EntityData data, EntityID id, PlayerID owner)
        implements Serializable {
    public EntityType type() { return data.type(); }

    public List<Component> components() { return data.components(); }

    public static Entity of(EntityType type, EntityID id, PlayerID owner) {
        return new Entity(EntityData.ofType(type), id, owner);
    }
}
