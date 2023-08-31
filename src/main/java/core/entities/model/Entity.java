package core.entities.model;

import core.model.EntityID;
import core.model.PlayerID;

import java.io.Serializable;

/**
 * Represents an existing, individual entity instance that has id and owner
 */
public record Entity(Components data, EntityID id, PlayerID owner) implements Serializable { }
