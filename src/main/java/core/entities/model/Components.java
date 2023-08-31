package core.entities.model;

import core.entities.components.Component;

import java.io.Serializable;
import java.util.List;

/**
 * Represents data of a generic entity i.e. all information apart from id and owner.
 */
public record Components(List<Component> components) implements Serializable {
    public static Components of(Component... components) {
        return new Components(List.of(components));
    }
}
