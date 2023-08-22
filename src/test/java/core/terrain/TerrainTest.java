package core.terrain;

import core.model.Position;
import core.terrain.model.TerrainSize;
import core.terrain.model.TerrainType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static core.terrain.model.TerrainType.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TerrainTest
{
    static Map<Position, TerrainType> MAP_0 = Map.of(
            new Position(0, 0), VOID,
            new Position(0, 1), WATER,
            new Position(1, 0), LAND,
            new Position(1, 1), LAND
    );

    @Test
    void map_has_correct_size()
    {
        // given
        TerrainSize givenSize = new TerrainSize(2, 2);
        Terrain map = new Terrain(givenSize, MAP_0);

        // when
        TerrainSize actualSize = map.size();
        assertThat(actualSize).isEqualTo(givenSize);
    }

    @Test
    void map_returns_correct_terrain()
    {
        // given
        TerrainSize givenSize = new TerrainSize(2, 2);
        Terrain map = new Terrain(givenSize, MAP_0);

        // then
        assertThat(map.terrainAt(new Position(0, 0))).isEqualTo(VOID);
        assertThat(map.terrainAt(new Position(0, 1))).isEqualTo(WATER);
        assertThat(map.terrainAt(new Position(1, 0))).isEqualTo(LAND);
        assertThat(map.terrainAt(new Position(1, 1))).isEqualTo(LAND);
    }

    @Test
    void outside_of_map_is_void()
    {
        // given
        TerrainSize givenSize = new TerrainSize(2, 2);
        Terrain map = new Terrain(givenSize, MAP_0);

        // then
        assertThat(map.terrainAt(new Position(2, 2))).isEqualTo(VOID);
    }

    @Test
    void terrain_is_updated_when_set()
    {
        // given
        TerrainSize givenSize = new TerrainSize(2, 2);
        Terrain map = new Terrain(givenSize, MAP_0);

        // when
        map.setTerrainAt(new Position(0, 0), WATER);

        // then
        assertThat(map.terrainAt(new Position(0, 0))).isEqualTo(WATER);
    }

    @Test
    void terrain_is_not_changed_outside_map()
    {
        // given
        TerrainSize givenSize = new TerrainSize(2, 2);
        Terrain map = new Terrain(givenSize, MAP_0);

        // when
        map.setTerrainAt(new Position(2, 2), WATER);

        // then
        assertThat(map.terrainAt(new Position(2, 2))).isEqualTo(VOID);
    }
}