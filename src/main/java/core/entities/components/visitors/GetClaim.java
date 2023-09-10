package core.entities.components.visitors;

import core.entities.components.Claim;
import core.entities.components.ComponentVisitor;
import core.entities.model.Entity;

import java.io.Serializable;
import java.util.Objects;

public class GetClaim implements ComponentVisitor<Claim>, Serializable {
    @Override
    public Claim visit(Claim component) { return component; }

    public Claim getClaim(Entity e) {
        return e.components()
                .stream()
                .map(c -> c.accept(this))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
