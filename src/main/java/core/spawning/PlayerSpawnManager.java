package core.spawning;

import core.claiming.ClaimedAreaView;
import core.entities.EntityBoardView;
import core.entities.model.EntityData;
import core.entities.model.EntityType;
import core.entities.model.components.Cost;
import core.fogofwar.PlayerFogOfWarView;
import core.model.PlayerID;
import core.model.Position;
import core.resources.PlayerResourcesView;
import core.terrain.TerrainView;
import core.turns.TurnView;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class PlayerSpawnManager implements Serializable {
    private final PlayerID player;
    private final EntityBoardView entityBoard;
    private final PlayerFogOfWarView fow;
    private final ClaimedAreaView claimedArea;
    private final PlayerResourcesView resources;
    private final TerrainView terrain;
    private final TurnView turnView;

    public List<Position> allowedSpawnPositions(EntityType type) {
        if (!turnView.currentPlayer().equals(player))
            return List.of();
        else if (!canAfford(type))
            return List.of();
        return fow.visiblePositions()
                .stream()
                .filter(terrain::contains)
                .filter(this::owns)
                .filter(this::unoccupied)
                .toList();
    }

    private boolean canAfford(EntityType type) {
        EntityData data = EntityData.ofType(type);
        Optional<Cost> cost = data.getCost();
        return cost.filter(value -> resources.canAfford(value.resources())).isPresent();
    }

    private boolean unoccupied(Position position) {
        return entityBoard.entitiesAt(position).isEmpty();
    }

    private boolean owns(Position position) {
        return player.equals(claimedArea.owner(position).orElse(null));
    }
}
