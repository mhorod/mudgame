package core.entities.components.visitors;

import core.entities.components.Attack;
import core.entities.components.ComponentVisitor;
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
