package core;

import core.entities.EntityBoard;
import core.fogofwar.FogOfWar;
import core.terrain.Terrain;

import java.io.Serializable;

public record GameState(
        PlayerManager playerManager,
        EntityBoard entityBoard,
        FogOfWar fogOfWar,
        Terrain terrain
) implements Serializable { }
