package core.entities.model.components.visitors;

import core.entities.model.components.Component;
import core.entities.model.components.ComponentVisitor;
import core.entities.model.components.Cost;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GetCost implements ComponentVisitor<Optional<Cost>>, Serializable {
    @Override
    public Optional<Cost> visit(Cost c) { return Optional.of(c); }

    public Optional<Cost> getCost(List<Component> components) {
        return components
                .stream()
                .map(c -> c.accept(this))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(Optional.empty());
    }
}
