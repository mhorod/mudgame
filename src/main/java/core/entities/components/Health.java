package core.entities.components;

import lombok.Getter;

public final class Health implements Component {

    private final int maxHealth;
    @Getter
    private int currentHealth;

    public Health(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public void damage(int amount) {
        currentHealth -= amount;
    }

    public void heal(int amount) {
        currentHealth += amount;
    }

    public boolean alive() { return currentHealth > 0; }

    @Override
    public <T> T accept(ComponentVisitor<T> visitor) {
        return visitor.visit(this);
    }
}