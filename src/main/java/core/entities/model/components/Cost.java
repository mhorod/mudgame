package core.entities.model.components;

import core.resources.ResourceType;
import core.resources.Resources;

import java.util.Map;

public record Cost(Resources resources) implements Component {
    public static Cost of(Map<ResourceType, Integer> amount) {
        return new Cost(Resources.of(amount));
    }

    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
