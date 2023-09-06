package core.pathfinder;

import core.model.EntityID;
import core.model.Position;

public class UnreachablePosition extends RuntimeException {
    public UnreachablePosition(Position position) {
        super(String.format("Position %s is unreachable", position));
    }

    public UnreachablePosition(EntityID entityID, Position position) {
        super(String.format("Entity with id %s can't reach position %s", entityID, position));
    }
}
