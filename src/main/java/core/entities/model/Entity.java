package core.entities.model;

import core.entities.components.Component;
import core.model.EntityID;
import core.model.PlayerID;

import java.io.Serializable;
import java.util.List;

/**
 * Represents an existing, individual entity instance that has id and owner
 */
public record Entity(List<Component> components, EntityID id, PlayerID owner)
        implements Serializable { }
