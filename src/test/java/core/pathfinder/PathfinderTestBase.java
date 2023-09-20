package core.pathfinder;

import core.entities.model.components.Movement;
import core.entities.model.EntityData;
import core.entities.model.EntityType;
import core.model.Position;
import core.terrain.model.Terrain;
import core.terrain.model.TerrainSize;
import core.terrain.model.TerrainType;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static core.terrain.model.TerrainType.LAND;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;

public abstract class PathfinderTestBase {

    void assertThatPathIsContinuous(List<Position> path) {
        for (int i = 1; i < path.size(); i++)
            assertThatPositionsAreNeighboring(path.get(i - 1), path.get(i));
    }

    private void assertThatPositionsAreNeighboring(Position prev, Position next) {
        int dx = next.x() - prev.x();
        int dy = next.y() - prev.y();
        if (dx * dy != 0 && Math.abs(dx + dy) != 1)
            fail("Positions %s and %s are not neighboring", prev, next);
    }

    Terrain simpleTerrain() {
        TerrainSize size = new TerrainSize(5, 5);
        Map<Position, TerrainType> map = IntStream.range(0, size.width())
                .boxed()
                .flatMap(
                        x -> IntStream.range(0, size.height())
                                .mapToObj(y -> new Position(x, y))
                )
                .collect(
                        toMap(p -> p, p -> LAND)
                );
        return new Terrain(size, map);
    }

    EntityData entityWithMovement(int movement) {
        return new EntityData(
                mock(EntityType.class),
                List.of(new Movement(movement))
        );
    }
}
