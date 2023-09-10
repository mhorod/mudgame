package mudgame.server.actions.entities;

import core.event.Action;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.actions.MoveEntity;
import mudgame.server.ServerGameState;
import mudgame.server.actions.Sender;

public final class EntityActionProcessor {
    private final EntityCreator entityCreator;
    private final EntityMover entityMover;
    private final EntityAttacker entityAttacker;

    public EntityActionProcessor(ServerGameState state, Sender sender) {
        EntityManager entityManager = new EntityManager(
                state.entityBoard(),
                state.fogOfWar(),
                state.claimedArea()
        );
        Visibility visibility = new Visibility(state.entityBoard(), state.terrain());
        entityCreator = new EntityCreator(sender, entityManager, state.fogOfWar(), visibility);
        entityMover = new EntityMover(state, sender);
        entityAttacker = new EntityAttacker(sender, state.entityBoard(), state.fogOfWar(),
                                            entityManager, visibility);
    }

    public void process(Action action) {
        if (action instanceof CreateEntity a)
            entityCreator.createEntity(a);
        else if (action instanceof MoveEntity a)
            entityMover.moveEntity(a);
        else if (action instanceof AttackEntityAction a)
            entityAttacker.attackEntity(a);
    }
}
