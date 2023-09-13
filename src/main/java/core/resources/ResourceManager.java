package core.resources;

import core.model.PlayerID;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class ResourceManager implements ResourcesView, Serializable {
    private final Map<PlayerID, PlayerResourceManager> playerManagers;

    public ResourceManager(List<PlayerID> players) {
        playerManagers = players.stream().collect(toMap(p -> p, p -> new PlayerResourceManager()));
    }

    public boolean canAfford(PlayerID player, Resources cost) {
        return playerManagers.get(player).canAfford(cost);
    }

    public void add(PlayerID player, Resources resources) {
        playerManagers.get(player).add(resources);
    }

    public void subtract(PlayerID player, Resources resources) {
        playerManagers.get(player).subtract(resources);
    }

    @Override
    public PlayerResourceManager playerResources(PlayerID p) {
        return playerManagers.get(p);
    }

    public void set(PlayerID player, ResourceType type, int amount) {
        playerManagers.get(player).set(type, amount);
    }
}
