package core.entities.components;

import lombok.Getter;

public class Movement implements Component {

    private final int movementPerTurn;

    @Getter
    private int currentMovement;

    public Movement(int movementPerTurn) {
        this.movementPerTurn = movementPerTurn;
        this.currentMovement = movementPerTurn;
    }

    void newTurn() {
        currentMovement = movementPerTurn;
    }

    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
