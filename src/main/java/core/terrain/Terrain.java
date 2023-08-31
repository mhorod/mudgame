package core.terrain;

import core.fogofwar.PlayerFogOfWar;
import core.model.Position;
import core.terrain.model.TerrainSize;
import core.terrain.model.TerrainType;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static core.terrain.model.TerrainType.UNKNOWN;
import static core.terrain.model.TerrainType.VOID;

@EqualsAndHashCode
public class Terrain implements TerrainView, Serializable {
    private final TerrainSize size;
    private final Map<Position, TerrainType> terrainMap;

    public Terrain(TerrainSize size, Map<Position, TerrainType> terrainMap) {
        this.size = size;
        this.terrainMap = new HashMap<>(terrainMap);
    }

    public Terrain applyFogOfWar(PlayerFogOfWar fow) {
        var newMap = terrainMap.keySet().stream().collect(Collectors.toMap(p -> p, p -> {
            if (fow.isVisible(p))
                return terrainMap.get(p);
            else
                return UNKNOWN;
        }));
        return new Terrain(size, newMap);
    }

    @Override
    public TerrainSize size() {
        return size;
    }

    @Override
    public TerrainType terrainAt(Position position) {
        return terrainMap.getOrDefault(position, VOID);
    }

    void setTerrainAt(Position position, TerrainType terrainType) {
        terrainMap.computeIfPresent(position, (pos, old) -> terrainType);
    }
}
