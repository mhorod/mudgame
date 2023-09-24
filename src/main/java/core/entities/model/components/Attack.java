package core.entities.model.components;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public final class Attack implements Component {

    private final int damage;
    private final int range;
    private final int attacksPerTurn;
    private int attacksLeft;

    public Attack(int damage, int range, int attacksPerTurn) {
        this.damage = damage;
        this.range = range;
        this.attacksPerTurn = attacksPerTurn;
        this.attacksLeft = attacksPerTurn;
    }

    public void attack() {
        attacksLeft--;
    }

    public void newTurn() {
        attacksLeft = attacksPerTurn;
    }

    public int damage() { return damage; }

    public int range() { return range; }

    public int attacksPerTurn() { return attacksPerTurn; }

    public int attacksLeft() { return attacksLeft; }


    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
