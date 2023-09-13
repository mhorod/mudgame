package core.entities.model.components.visitors;

import core.entities.model.components.Component;
import core.entities.model.components.ComponentVisitor;
import core.entities.model.components.Health;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GetHealth implements ComponentVisitor<Optional<Health>>, Serializable {
    @Override
    public Optional<Health> visit(Health h) { return Optional.of(h); }

    public Optional<Health> getHealth(List<Component> components) {
        return components
                .stream()
                .map(c -> c.accept(this))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(Optional.empty());
    }
}
