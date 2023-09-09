package mudgame.client.events;

import core.event.Event;
import core.turns.CompleteTurn;
import mudgame.client.ClientGameState;
import mudgame.controls.events.AttackEntityEvent;
import mudgame.controls.events.KillEntity;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.PlaceEntity;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.SpawnEntity;

public final class EventProcessor {
    private final ClientGameState state;
    private final EntityEventProcessor entityEventProcessor;

    public EventProcessor(ClientGameState state) {
        this.state = state;
        entityEventProcessor = new EntityEventProcessor(state);
    }

    public void process(Event event) {
        if (event instanceof SpawnEntity e)
            entityEventProcessor.spawnEntity(e);
        else if (event instanceof PlaceEntity e)
            entityEventProcessor.placeEntity(e);
        else if (event instanceof RemoveEntity e)
            entityEventProcessor.removeEntity(e);
        else if (event instanceof MoveEntityAlongPath e)
            entityEventProcessor.moveEntityAlongPath(e);
        else if (event instanceof CompleteTurn)
            completeTurn();
        else if (event instanceof AttackEntityEvent e)
            entityEventProcessor.attackEntity(e);
        else if (event instanceof KillEntity e)
            entityEventProcessor.killEntity(e);
    }

    private void completeTurn() {
        state.playerManager().completeTurn();
    }
}
