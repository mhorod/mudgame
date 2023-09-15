package core.resources;

import java.io.Serializable;

public class PlayerResourceManager implements PlayerResourcesView, Serializable {
    private final Resources resources;

    public PlayerResourceManager() {
        resources = new Resources();
    }

    public boolean canAfford(Resources cost) {
        return resources.isGreaterThanOrEqual(cost);
    }

    public void add(Resources resources) {
        this.resources.add(resources);
    }

    public void subtract(Resources resources) {
        this.resources.subtract(resources);
    }

    public void set(ResourceType type, int amount) {
        resources.set(type, amount);
    }

    public int amount(ResourceType resourceType) {
        return resources.amount(resourceType);
    }
}
