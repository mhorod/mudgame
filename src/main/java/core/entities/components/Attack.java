package core.entities.components;

public record Attack(int damage) implements Component {
    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
