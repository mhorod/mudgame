package core.pathfinder;

import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.fogofwar.FogOfWar;
import core.model.PlayerID;
import core.model.Position;
import core.terrain.model.Terrain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EntityPathfinderTest extends PathfinderTestBase {
    EntityBoard entityBoard;
    Terrain terrain;
    Pathfinder pathfinder;
    FogOfWar fow;

    @BeforeEach
    void init() {
        entityBoard = new EntityBoard();
        terrain = simpleTerrain();
        fow = mock(FogOfWar.class);
        when(fow.isVisible(any(), any())).thenReturn(true);

        pathfinder = new EntityPathfinder(terrain, entityBoard, fow);
    }

    @Test
    void finds_reachable_positions() {
        // given
        fow = mock(FogOfWar.class);

        int movement = 2;
        Entity entity = entityBoard.createEntity(
                entityWithMovement(movement),
                new PlayerID(0),
                new Position(0, 0)
        );
        // when
        ReachablePositions positions = pathfinder.reachablePositions(entity.id());

        // then
        assertThat(positions.getPositions()).hasSize(6);
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
        List<Position> path = pathfinder.findPath(entity.id(), new Position(1, 1));

        // then
        assertThat(path).hasSize(3);
        assertThatPathIsContinuous(path);
        assertThat(path)
                .startsWith(new Position(0, 0))
                .endsWith(new Position(1, 1));
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
        List<Position> path = positions.getPath(new Position(1, 1));

        // then
        assertThat(path).hasSize(3);
        assertThatPathIsContinuous(path);
        assertThat(path)
                .startsWith(new Position(0, 0))
                .endsWith(new Position(1, 1));
    }

    @Test
    void avoids_tiles_in_fog_of_war() {
        // given
        when(fow.isVisible(eq(new Position(0, 1)), any())).thenReturn(false);

        int movement = 5;
        Entity entity = entityBoard.createEntity(
                entityWithMovement(movement),
                new PlayerID(0),
                new Position(0, 0)
        );

        // when
        ReachablePositions positions = pathfinder.reachablePositions(entity.id());
        List<Position> path = positions.getPath(new Position(0, 2));

        // then
        assertThat(path).hasSize(5);
        assertThatPathIsContinuous(path);
        assertThat(path)
                .startsWith(new Position(0, 0))
                .endsWith(new Position(0, 2));
    }


}