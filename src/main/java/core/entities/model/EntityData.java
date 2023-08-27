package core.entities.model;

import core.entities.components.Component;

import java.util.List;

/**
 * Represents data of a generic entity i.e. all information apart from id and owner.
 */
public interface EntityData {
    List<Component> components();
}
