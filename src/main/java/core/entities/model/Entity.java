package core.entities.model;

import core.model.EntityID;
import core.model.PlayerID;

/**
 * Represents an existing, individual entity instance that has id and owner
 */
public record Entity(EntityData data, EntityID id, PlayerID owner) { }
