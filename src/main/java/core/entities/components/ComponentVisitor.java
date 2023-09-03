package core.entities.components;

public interface ComponentVisitor<T> {
    default T visit(Component component) {
        return null;
    }
    default T visit(Vision component) {
        return null;
    }
    default T visit(Movement component) {
        return null;
    }
    default T visit(Health component) {
        return null;
    }
    default T visit(Attack component) {
        return null;
    }
}
