package core.pathfinder;

import core.entities.EntityBoard;
import core.entities.model.Entity;
import core.fogofwar.PlayerFogOfWar;
import core.model.PlayerID;
import core.model.Position;
import core.terrain.model.Terrain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerPathfinderTest extends PathfinderTestBase {
    EntityBoard entityBoard;
    Terrain terrain;
    PlayerPathfinder pathfinder;
    PlayerFogOfWar fow;

    @BeforeEach
    void init() {
        entityBoard = new EntityBoard();
        terrain = simpleTerrain();
        fow = mock(PlayerFogOfWar.class);
        when(fow.isVisible(any())).thenReturn(true);
        when(fow.playerID()).thenReturn(new PlayerID(0));
        pathfinder = new PlayerPathfinder(new PlayerID(0), terrain, entityBoard, fow);
    }

    @Test
    void finds_path_for_player_owning_entity() {
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
    void does_not_find_path_for_unowned_entity() {
        // given
        int movement = 2;
        Entity entity = entityBoard.createEntity(
                entityWithMovement(movement),
                new PlayerID(1),
                new Position(0, 0)
        );

        // when
        ReachablePositions positions = pathfinder.reachablePositions(entity.id());


        // then
        assertThat(positions.getPositions()).isEmpty();
    }

}