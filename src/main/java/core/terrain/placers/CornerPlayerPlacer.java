package core.terrain.placers;

import core.model.Position;
import core.terrain.model.Terrain;

import java.util.List;
import java.util.stream.Stream;

public class CornerPlayerPlacer implements PlayerPlacer {
    @Override
    public List<Position> placePlayers(
            int playerCount, Terrain terrain
    ) {
        int w = terrain.size().width() - 1;
        int h = terrain.size().height() - 1;
        return Stream.of(
                new Position(0, 0),
                new Position(w, h),
                new Position(0, h),
                new Position(w, 0)
        ).limit(playerCount).toList();
    }
}
