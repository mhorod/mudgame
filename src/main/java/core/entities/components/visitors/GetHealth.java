package core.entities.components.visitors;

import core.entities.components.ComponentVisitor;
import core.entities.components.Health;
import core.entities.model.Entity;

import java.util.Objects;

public class GetHealth implements ComponentVisitor<Integer> {
    @Override
    public Integer visit(Health h) { return h.getCurrentHealth(); }

    public Integer getHealth(Entity e) {
        return e.components()
                .stream()
                .map(c -> c.accept(this))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
