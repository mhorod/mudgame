package core.entities;

import core.id.EntityID;
import core.id.PlayerID;

/**
 * Represents an existing, individual entity instance that has id and owner
 */
public record Entity(EntityData data, EntityID id, PlayerID owner)
{ }
