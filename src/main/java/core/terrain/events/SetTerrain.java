package core.terrain.events;

import core.events.Event;
import core.model.Position;
import core.terrain.model.TerrainType;

import java.io.Serializable;
import java.util.List;

public record SetTerrain(List<SetPositionTerrain> positions) implements Event {
    public record SetPositionTerrain(Position position, TerrainType terrainType)
            implements Serializable { }
}
