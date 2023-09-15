package core.entities.model.components.visitors;

import core.entities.model.components.Component;
import core.entities.model.components.ComponentVisitor;
import core.entities.model.components.Production;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GetProduction implements ComponentVisitor<Optional<Production>>, Serializable {
    @Override
    public Optional<Production> visit(Production p) { return Optional.of(p); }

    public Optional<Production> getProduction(List<Component> components) {
        return components
                .stream()
                .map(c -> c.accept(this))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(Optional.empty());
    }
}
