package core.fogofwar;

import core.entities.model.Entity;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class FogOfWar implements FogOfWarView, Serializable {
    private final Map<PlayerID, PlayerFogOfWar> fows;

    public FogOfWar(List<PlayerID> players) {
        fows = players.stream().collect(Collectors.toMap(p -> p, PlayerFogOfWar::new));
    }

    private FogOfWar(Map<PlayerID, PlayerFogOfWar> fows) {
        this.fows = fows;
    }

    public static FogOfWar from(PlayerFogOfWar fow) {
        return new FogOfWar(Map.of(fow.playerID(), fow));
    }

    @Override
    public boolean playerSees(Position position, PlayerID viewer) {
        return fows.get(viewer).isVisible(position);
    }

    @Override
    public Set<PlayerID> players() {
        return fows.keySet();
    }

    public PlayerFogOfWar playerFogOfWar(PlayerID id) {
        return fows.get(id);
    }

    @Override
    public PlayerFogOfWarView playerFogOfWarView(PlayerID id) { return fows.get(id); }

    public void placeEntity(Entity entity, Position position) {
        for (PlayerFogOfWar fow : fows.values())
            fow.placeEntity(entity, position);
    }

    public void moveEntity(EntityID entityID, Position position) {
        for (PlayerFogOfWar fow : fows.values())
            fow.moveEntity(entityID, position);
    }

    public void removeEntity(EntityID entityID) {
        for (PlayerFogOfWar fow : fows.values())
            fow.removeEntity(entityID);
    }
}
