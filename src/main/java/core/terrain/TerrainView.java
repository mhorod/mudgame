package core.terrain;

import core.model.Position;
import core.terrain.model.TerrainSize;
import core.terrain.model.TerrainType;

public interface TerrainView {
    TerrainSize size();
    TerrainType terrainAt(Position position);
    boolean contains(Position position);
}
