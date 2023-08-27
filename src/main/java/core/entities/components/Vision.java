package core.entities.components;

public record Vision(int range) implements Component {
    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
