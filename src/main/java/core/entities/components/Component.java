package core.entities.components;

import java.io.Serializable;

public interface Component extends Serializable {
    <T> T accept(ComponentVisitor<T> visitor);
}
