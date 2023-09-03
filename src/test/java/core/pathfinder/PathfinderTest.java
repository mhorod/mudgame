package core.pathfinder;

import core.entities.EntityBoard;
import core.entities.components.Movement;
import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.entities.model.EntityType;
import core.model.PlayerID;
import core.model.Position;
import core.pathfinder.Pathfinder.ReachablePositions;
import core.terrain.Terrain;
import core.terrain.model.TerrainSize;
import core.terrain.model.TerrainType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static core.terrain.model.TerrainType.LAND;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PathfinderTest {

    EntityBoard entityBoard;
    Terrain terrain;
    Pathfinder pathfinder;

    @BeforeEach
    void init() {
        entityBoard = new EntityBoard();
        terrain = simpleTerrain();
        pathfinder = new Pathfinder(terrain, entityBoard);
    }

    @Test
    void finds_reachable_positions() {
        // given
        int movement = 2;
        Entity entity = entityBoard.createEntity(
                entityWithMovement(movement),
                new PlayerID(0),
                new Position(0, 0)
        );

        // when
        ReachablePositions positions = pathfinder.reachablePositions(entity.id());

        // then
        assertThat(positions.getPositions()).hasSize(9);
    }

    @Test
    void finds_path() {
        // given
        int movement = 2;
        Entity entity = entityBoard.createEntity(
                entityWithMovement(movement),
                new PlayerID(0),
                new Position(0, 0)
        );

        // when
        List<Position> path = pathfinder.findPath(entity.id(), new Position(2, 2));

        // then
        assertThat(path).hasSize(3);
    }

    @Test
    void finds_path_for_reachable_positions() {
        // given
        int movement = 2;
        Entity entity = entityBoard.createEntity(
                entityWithMovement(movement),
                new PlayerID(0),
                new Position(0, 0)
        );

        // when
        ReachablePositions positions = pathfinder.reachablePositions(entity.id());

        // then
        assertThat(positions.getPath(new Position(1, 1))).hasSize(2);
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