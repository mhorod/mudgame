package mudgame.server.actions.entities;

import mudgame.controls.actions.Action;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.actions.MoveEntity;
import mudgame.server.actions.Sender;
import mudgame.server.internal.InteractiveState;

public final class EntityActionProcessor {
    private final CreationProcessor creationProcessor;
    private final MoveProcessor moveProcessor;
    private final AttackProcessor attackProcessor;

    public EntityActionProcessor(InteractiveState state, Sender sender) {
        creationProcessor = new CreationProcessor(state, sender);
        moveProcessor = new MoveProcessor(state, sender);
        attackProcessor = new AttackProcessor(state, sender);
    }

    public void process(Action action) {
        if (action instanceof CreateEntity a)
            creationProcessor.createEntity(a);
        else if (action instanceof MoveEntity a)
            moveProcessor.moveEntity(a);
        else if (action instanceof AttackEntityAction a)
            attackProcessor.attackEntity(a);
    }
}
