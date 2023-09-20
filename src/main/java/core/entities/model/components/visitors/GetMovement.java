package core.entities.model.components.visitors;

import core.entities.model.components.Component;
import core.entities.model.components.ComponentVisitor;
import core.entities.model.components.Movement;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GetMovement implements ComponentVisitor<Optional<Movement>>, Serializable {
    @Override
    public Optional<Movement> visit(Movement c) { return Optional.of(c); }

    public Optional<Movement> getMovement(List<Component> components) {
        return components
                .stream()
                .map(c -> c.accept(this))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(Optional.empty());
    }
}
