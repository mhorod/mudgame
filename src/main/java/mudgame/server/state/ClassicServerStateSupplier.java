package mudgame.server.state;

import core.entities.model.Entity;
import core.model.PlayerID;
import core.model.Position;
import core.terrain.generators.RectangleLandGenerator;
import core.terrain.generators.StartingTerrainGenerator;
import core.terrain.generators.TerrainGenerator;
import core.terrain.model.StartingTerrain;
import core.terrain.placers.PlayerPlacer;
import core.terrain.placers.RandomPlayerPlacer;
import lombok.RequiredArgsConstructor;
import mudgame.server.gameover.OwnsThreeEntities;
import mudgame.server.rules.DefaultRules;

import java.util.List;

import static core.entities.model.EntityType.BASE;
import static core.resources.ResourceType.MUD;

@RequiredArgsConstructor
public class ClassicServerStateSupplier implements ServerStateSupplier {
    @Override
    public ServerState get(int playerCount) {
        StartingTerrain generatedTerrain = defaultTerrainGenerator().generate(playerCount);
        ServerState state = ServerState.of(
                playerCount,
                generatedTerrain.terrain(),
                OwnsThreeEntities::new,
                new DefaultRules()
        );
        placePlayerBases(state, generatedTerrain.startingPositions());
        initializeResources(state);
        return state;
    }

    private static void initializeResources(ServerState state) {
        state.turnManager().players()
                .forEach(p -> state.resourceManager().add(p, 10, MUD));
    }

    private static void placePlayerBases(ServerState state, List<Position> startingLocations) {
        for (int i = 0; i < startingLocations.size(); i++)
            placeBase(state, i, startingLocations.get(i));
    }

    private static void placeBase(ServerState state, int i, Position position) {
        PlayerID owner = state.turnManager().players().get(i);
        Entity entity = state.entityBoard().createEntity(BASE, owner, position);
        state.fogOfWar().playerFogOfWar(owner).placeEntity(entity, position);
        state.claimedArea().placeEntity(entity, position);
    }

    private static StartingTerrainGenerator defaultTerrainGenerator() {
        PlayerPlacer playerPlacer = new RandomPlayerPlacer(2, 4);
        TerrainGenerator terrainGenerator = new RectangleLandGenerator(100);

        return StartingTerrainGenerator.of(terrainGenerator, playerPlacer);
    }
}
