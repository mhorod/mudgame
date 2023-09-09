package core.entities.components;

public record Attack(int damage, int range) implements Component {
    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
