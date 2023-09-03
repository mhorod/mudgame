package core.entities.components;

import lombok.Getter;

public final class Movement implements Component {

    private final int movementPerTurn;

    @Getter
    private int currentMovement;

    public Movement(int movementPerTurn) {
        this.movementPerTurn = movementPerTurn;
        this.currentMovement = movementPerTurn;
    }

    public void newTurn() {
        currentMovement = movementPerTurn;
    }

    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
