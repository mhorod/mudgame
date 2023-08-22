package core.terrain;

import core.model.Position;
import core.terrain.model.TerrainSize;
import core.terrain.model.TerrainType;

import java.util.HashMap;
import java.util.Map;

import static core.terrain.model.TerrainType.VOID;

public class Terrain implements TerrainView {
    private final TerrainSize size;
    private final Map<Position, TerrainType> terrainMap;

    public Terrain(TerrainSize size, Map<Position, TerrainType> terrainMap) {
        this.size = size;
        this.terrainMap = new HashMap<>(terrainMap);
    }

    @Override
    public TerrainSize size() {
        return size;
    }

    @Override
    public TerrainType terrainAt(Position position) {
        return terrainMap.getOrDefault(position, VOID);
    }

    public void setTerrainAt(Position position, TerrainType terrainType) {
        terrainMap.computeIfPresent(position, (pos, old) -> terrainType);
    }
}
