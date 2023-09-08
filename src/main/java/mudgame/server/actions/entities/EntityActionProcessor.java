package mudgame.server.actions.entities;

import core.event.Action;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.actions.MoveEntity;
import mudgame.server.ServerGameState;
import mudgame.server.actions.Sender;

public final class EntityActionProcessor {
    private final EntityCreator entityCreator;
    private final EntityMover entityMover;

    public EntityActionProcessor(ServerGameState state, Sender sender) {
        EntityManager entityManager = new EntityManager(state.entityBoard(), state.fogOfWar());
        Visibility visibility = new Visibility(state.entityBoard(), state.terrain());
        entityCreator = new EntityCreator(sender, entityManager, state.fogOfWar(), visibility);
        entityMover = new EntityMover(state, sender);
    }

    private void createEntity(CreateEntity a) {
        entityCreator.createEntity(a);
    }

    private void moveEntity(MoveEntity a) {
        entityMover.moveEntity(a);
    }

    public void process(Action action) {
        if (action instanceof CreateEntity a)
            createEntity(a);
        else if (action instanceof MoveEntity a)
            moveEntity(a);
    }
}
