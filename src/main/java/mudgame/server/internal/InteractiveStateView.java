package mudgame.server.internal;

import core.claiming.ClaimChange;
import core.entities.model.Entity;
import core.fogofwar.PlayerFogOfWarView;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.pathfinder.EntityPathfinder;
import core.pathfinder.Pathfinder;
import core.terrain.TerrainView;
import core.terrain.model.TerrainType;
import lombok.RequiredArgsConstructor;
import mudgame.server.state.ServerState;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Facade for the server game state.
 * <p>
 * The interface of this class is big, but attempts to split it into smaller interfaces
 * shown that due to game logic they are mostly used all together.
 */
@RequiredArgsConstructor
public class InteractiveStateView {
    private final ServerState state;

    // turns
    public List<PlayerID> players() {
        return state.turnManager().players();
    }

    public PlayerID currentPlayer() {
        return state.turnManager().currentPlayer();
    }

    // terrain
    public boolean terrainContains(Position position) {
        return state.terrain().contains(position);
    }

    public TerrainType terrainAt(Position position) {
        return state.terrain().terrainAt(position);
    }

    // entities
    public boolean containsEntity(EntityID id) {
        return state.entityBoard().containsEntity(id);
    }

    public Position entityPosition(EntityID entityID) {
        return state.entityBoard().entityPosition(entityID);
    }

    public PlayerID entityOwner(EntityID entityID) {
        return state.entityBoard().entityOwner(entityID);
    }

    public List<Entity> entitiesAt(Position position) {
        return state.entityBoard().entitiesAt(position);
    }

    public Entity findEntityByID(EntityID entityID) {
        return state.entityBoard().findEntityByID(entityID);
    }

    // Fog of war
    public boolean playerSees(PlayerID player, Position position) {
        return state.fogOfWar().playerSees(position, player);
    }

    public boolean playerSeesAny(PlayerID player, Position... positions) {
        return Arrays.stream(positions).anyMatch(p -> playerSees(player, p));
    }

    // claiming
    public Optional<PlayerID> owner(Position position) {
        return state.claimedArea().owner(position);
    }

    public ClaimChange maskedFor(PlayerID player, ClaimChange claimChange) {
        return claimChange.masked(state.fogOfWar().playerFogOfWar(player),
                                  state.terrain());
    }

    public Pathfinder pathfinder() {
        return new EntityPathfinder(
                state.terrain(),
                state.entityBoard(),
                state.fogOfWar()
        );
    }

    public boolean isGameOver() {
        return state.gameOverCondition().isGameOver();
    }

    public Optional<List<PlayerID>> winners() {
        return state.gameOverCondition().winners();
    }

    public List<Entity> playerEntities(PlayerID player) {
        return state.entityBoard().playerEntities(player);
    }

    public PlayerFogOfWarView playerFow(PlayerID player) {
        return state.fogOfWar().playerFogOfWar(player);
    }

    public TerrainView terrain() {
        return state.terrain();
    }
}
