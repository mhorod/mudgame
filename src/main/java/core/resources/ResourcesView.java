package core.resources;

import core.model.PlayerID;

public interface ResourcesView {
    boolean canAfford(PlayerID player, Resources cost);
    PlayerResourcesView playerResources(PlayerID p);
}
