package core.spawning;

import core.claiming.ClaimedAreaView;
import core.entities.EntityBoardView;
import core.entities.model.EntityType;
import core.fogofwar.PlayerFogOfWarView;
import core.model.PlayerID;
import core.model.Position;
import core.terrain.TerrainView;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
public class PlayerSpawnManager implements Serializable {
    private final PlayerID player;
    private final EntityBoardView entityBoard;
    private final PlayerFogOfWarView fow;
    private final ClaimedAreaView claimedArea;
    private final TerrainView terrain;

    public List<Position> allowedSpawnPositions(EntityType type) {
        return fow.visiblePositions()
                .stream()
                .filter(terrain::contains)
                .filter(this::owns)
                .filter(this::unoccupied)
                .toList();
    }


    private boolean unoccupied(Position position) {
        return entityBoard.entitiesAt(position).isEmpty();
    }

    private boolean owns(Position position) {
        return player.equals(claimedArea.owner(position).orElse(null));
    }
}
