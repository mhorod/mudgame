package core.terrain.events;

import core.events.Event;
import core.model.Position;
import core.terrain.model.TerrainType;

public record SetTerrain(Position position, TerrainType terrainType) implements Event { }
