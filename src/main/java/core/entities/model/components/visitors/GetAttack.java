package core.entities.model.components.visitors;

import core.entities.model.components.Attack;
import core.entities.model.components.Component;
import core.entities.model.components.ComponentVisitor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GetAttack implements ComponentVisitor<Optional<Attack>>, Serializable {
    @Override
    public Optional<Attack> visit(Attack a) { return Optional.of(a); }

    public Optional<Attack> getAttack(List<Component> components) {
        return components
                .stream()
                .map(c -> c.accept(this))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(Optional.empty());
    }
}
