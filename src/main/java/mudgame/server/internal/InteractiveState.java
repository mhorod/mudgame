package mudgame.server.internal;

import core.entities.model.EntityType;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import mudgame.server.ServerGameState;

/**
 * Functional facade for the {@link ServerGameState}.
 * <p>
 * This is meant for internal use to simplify access and modifications on the game state.
 * <p>
 * As {@link InteractiveStateView} this interface is big and collects all logic of the game in one place.
 * It is justified by the fact that actions influence most of the core components, and it is simpler to have one
 * class that manages access to them.
 */
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
}
