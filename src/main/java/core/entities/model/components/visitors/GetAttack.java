package core.entities.model.components.visitors;

import core.entities.model.components.Attack;
import core.entities.model.components.ComponentVisitor;
import core.entities.model.Entity;

import java.io.Serializable;
import java.util.Objects;

public class GetAttack implements ComponentVisitor<Attack>, Serializable {
    @Override
    public Attack visit(Attack a) { return a; }

    public Attack getAttack(Entity e) {
        return e.components()
                .stream()
                .map(c -> c.accept(this))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
