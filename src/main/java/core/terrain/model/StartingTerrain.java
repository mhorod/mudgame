package core.terrain.model;

import core.model.Position;

import java.util.List;

public record StartingTerrain(Terrain terrain, List<Position> startingPositions) {
}
