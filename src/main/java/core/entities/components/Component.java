package core.entities.components;

public interface Component {
    <T> T accept(ComponentVisitor<T> visitor);
}
