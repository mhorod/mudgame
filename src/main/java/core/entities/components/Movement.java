package core.entities.components;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public final class Movement implements Component {

    private final int movementPerTurn;

    @Getter
    private int currentMovement;

    public Movement(int movementPerTurn) {
        this.movementPerTurn = movementPerTurn;
        this.currentMovement = movementPerTurn;
    }

    public void move(int movementCost) {
        currentMovement -= movementCost;
    }

    public void newTurn() {
        currentMovement = movementPerTurn;
    }

    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Movement{" +
               "movementPerTurn=" + movementPerTurn +
               ", currentMovement=" + currentMovement +
               '}';
    }
}
