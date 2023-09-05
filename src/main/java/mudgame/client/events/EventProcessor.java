package mudgame.client.events;

import core.event.Event;
import mudgame.client.ClientGameState;
import mudgame.controls.events.MoveEntityAlongPath;
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
        else if (event instanceof MoveEntityAlongPath e)
            entityEventProcessor.moveEntityAlongPath(e);
    }
}
