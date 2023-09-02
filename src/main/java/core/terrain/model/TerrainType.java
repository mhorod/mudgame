package core.terrain.model;

import java.io.Serializable;

public enum TerrainType implements Serializable {
    UNKNOWN(-1),
    VOID(-1),
    WATER(-1),
    LAND(1),
    MOUNTAIN(2);


    TerrainType(int movementCost) {
    }
}
