package core.resources;

public interface PlayerResourcesView {
    boolean canAfford(Resources cost);
    int amount(ResourceType resourceType);
}
