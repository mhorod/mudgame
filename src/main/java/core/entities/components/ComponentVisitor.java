package core.entities.components;

public interface ComponentVisitor<T> {
    default T visit(Vision component) {
        return null;
    }
    default T visit(Movement component) {
        return null;
    }
}
