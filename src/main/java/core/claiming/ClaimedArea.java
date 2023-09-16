package core.claiming;

import core.entities.model.components.Claim;
import core.entities.model.components.visitors.GetClaim;
import core.entities.model.Entity;
import core.fogofwar.PlayerFogOfWarView;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.terrain.TerrainView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * Manages areas owned by players.
 */
public class ClaimedArea implements ClaimedAreaView, Serializable {
    private final Map<Position, PlayerID> claimed;
    private final Map<EntityID, List<Position>> claimedByEntity = new HashMap<>();

    private final GetClaim getClaim = new GetClaim();

    public ClaimedArea() {
        claimed = new HashMap<>();
    }


    public PlayerClaimedArea mask(PlayerFogOfWarView fow, TerrainView terrain) {
        Map<Position, PlayerID> maskedClaimed = claimed
                .entrySet()
                .stream()
                .filter(e -> fow.isVisible(e.getKey()))
                .filter(e -> terrain.contains(e.getKey()))
                .collect(toMap(Entry::getKey, Entry::getValue));
        return new PlayerClaimedArea(maskedClaimed);
    }

    @Override
    public Optional<PlayerID> owner(Position position) {
        if (claimed.containsKey(position))
            return Optional.of(claimed.get(position));
        else
            return Optional.empty();
    }

    @Override
    public List<ClaimedPosition> claimedPositions() {
        return claimed.entrySet()
                .stream()
                .map(e -> new ClaimedPosition(e.getKey(), e.getValue()))
                .toList();
    }

    /**
     * Sets positions to be owned by player.
     * If a position is already owner then the owner is overridden.
     */
    private ClaimChange claim(PlayerID player, List<Position> positions) {
        List<ClaimedPosition> claimedPositions = new ArrayList<>();
        for (Position position : positions) {
            claimed.put(position, player);
            claimedPositions.add(new ClaimedPosition(position, player));
        }
        return new ClaimChange(claimedPositions, List.of());
    }

    /**
     * Sets positions to be owned by no one
     */
    public ClaimChange unclaim(List<Position> positions) {
        for (Position position : positions)
            claimed.remove(position);
        return new ClaimChange(List.of(), positions);
    }

    public ClaimChange placeEntity(Entity entity, Position position) {
        Claim claim = getClaim.getClaim(entity);
        if (claim == null)
            return ClaimChange.empty();

        List<Position> positions = claimedArea(position, claim.range());
        claimedByEntity.put(entity.id(), positions);
        return claim(entity.owner(), positions);
    }

    public ClaimChange removeEntity(EntityID entityID) {
        if (!claimedByEntity.containsKey(entityID))
            return ClaimChange.empty();

        List<Position> positions = claimedByEntity.get(entityID);
        ClaimChange result = unclaim(positions);
        claimedByEntity.remove(entityID);

        return result;
    }


    private List<Position> claimedArea(Position center, int range) {
        List<Position> result = new ArrayList<>();
        for (int dx = -range; dx <= range; dx++)
            for (int dy = -range; dy <= range; dy++) {
                Position position = center.plus(dx, dy);
                if (!claimed.containsKey(position))
                    result.add(position);
            }
        return result;
    }

    public ClaimChange moveEntity(Entity entity, Position position) {
        ClaimChange removeChange = removeEntity(entity.id());
        ClaimChange placeChange = placeEntity(entity, position);
        return getClaimChange(removeChange, placeChange);
    }

    private ClaimChange getClaimChange(ClaimChange removeChange, ClaimChange placeChange) {
        Set<Position> claimedPositions = placeChange.claimedPositions()
                .stream()
                .map(ClaimedPosition::position)
                .collect(toSet());
        Set<Position> unclaimedPositions = new HashSet<>(removeChange.unclaimedPositions());
        unclaimedPositions.removeAll(claimedPositions);
        return new ClaimChange(
                placeChange.claimedPositions(),
                unclaimedPositions.stream().toList()
        );
    }
}
