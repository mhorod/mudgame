package core.terrain.placers;

import core.model.Position;
import core.terrain.model.Terrain;

import java.util.List;

public interface PlayerPlacer {
    List<Position> placePlayers(int playerCount, Terrain terrain);
}
