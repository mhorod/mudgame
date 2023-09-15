package mudgame.server.internal;

import core.entities.model.Entity;
import core.entities.model.EntityType;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.resources.Resources;
import lombok.extern.slf4j.Slf4j;
import mudgame.server.ServerGameState;

import java.util.List;
import java.util.Optional;

/**
 * Functional facade for the {@link ServerGameState}.
 * <p>
 * This is meant for internal use to simplify access and modifications on the game state.
 * <p>
 * As {@link InteractiveStateView} this interface is big and collects all logic of the game in one place.
 * It is justified by the fact that actions influence most of the core components, and it is simpler to have one
 * class that manages access to them.
 */
@Slf4j
public class InteractiveState extends InteractiveStateView {
    private final ServerGameState state;
    private final EntityCreator entityCreator;
    private final EntityMover entityMover;
    private final EntityRemover entityRemover;

    public InteractiveState(ServerGameState state) {
        super(state);
        this.state = state;
        Visibility visibility = new Visibility(
                state.terrain(),
                state.entityBoard(),
                state.claimedArea()
        );
        this.entityCreator = new EntityCreator(state, visibility);
        entityMover = new EntityMover(state, visibility);
        entityRemover = new EntityRemover(state, visibility);
    }

    public void completeTurn() {
        state.turnManager().completeTurn();
        log.debug("Completed turn. Current turn: {}, player: {}",
                  state.turnManager().currentTurn(),
                  state.turnManager().currentPlayer()
        );
    }

    public CreatedEntity createEntity(EntityType type, PlayerID owner, Position position) {
        return entityCreator.createEntity(type, owner, position);
    }


    public MovedEntity moveEntity(EntityID entityID, Position destination) {
        return entityMover.moveEntity(entityID, destination);
    }

    public RemovedEntity removeEntity(EntityID entityID) {
        return entityRemover.removeEntity(entityID);
    }

    public Resources produceResources(PlayerID player) {
        if (state.turnManager().currentTurn() < state.turnManager().playerCount())
            return Resources.empty();

        List<Resources> resources = state.entityBoard()
                .playerEntities(player)
                .stream()
                .map(Entity::getProduction)
                .flatMap(Optional::stream)
                .toList();
        resources.forEach(r -> state.resourceManager().add(player, r));
        return resources.stream().reduce(Resources.empty(), Resources::merge);
    }
}
