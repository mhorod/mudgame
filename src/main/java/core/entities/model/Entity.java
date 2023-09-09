package core.entities.model;

import core.entities.components.Component;
import core.entities.components.Health;
import core.model.EntityID;
import core.model.PlayerID;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Represents an existing, individual entity instance that has entityID and owner
 */
public record Entity(EntityData data, EntityID id, PlayerID owner)
        implements Serializable {
    public EntityType type() { return data.type(); }

    public List<Component> components() { return data.components(); }

    public static Entity of(EntityType type, EntityID id, PlayerID owner) {
        return new Entity(EntityData.ofType(type), id, owner);
    }

    public Optional<Integer> damage(int amount) {
        return components()
                .stream()
                .filter(Health.class::isInstance)
                .map(Health.class::cast)
                .map(h -> {
                    h.damage(amount);
                    return h.getCurrentHealth();
                })
                .findFirst();
    }
}
