package mudgame.server.actions.entities;

import core.event.EventOccurrence;
import core.model.PlayerID;
import core.model.Position;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.events.SpawnEntity;
import mudgame.controls.events.VisibilityChange;
import mudgame.server.actions.Sender;
import mudgame.server.internal.EntityCreator.CreatedEntity;
import mudgame.server.internal.InteractiveState;

/**
 * Creates new entities and sends the events
 */
final class CreationProcessor {
    private final InteractiveState state;
    private final Sender sender;

    public CreationProcessor(InteractiveState state, Sender sender) {
        this.state = state;
        this.sender = sender;
    }

    void createEntity(CreateEntity a) {
        CreatedEntity createdEntity = state.createEntity(a.type(), a.owner(), a.position());

        state.players()
                .stream()
                .filter(p -> state.playerSees(p, a.position()))
                .map(p -> eventOccurrenceFor(p, a, createdEntity))
                .forEach(sender::send);
    }

    private EventOccurrence eventOccurrenceFor(
            PlayerID player, CreateEntity action, CreatedEntity createdEntity
    ) {
        if (player.equals(action.owner()))
            return EventOccurrence.of(ownerEvent(createdEntity, action.position()), player);
        else
            return EventOccurrence.of(otherEvent(player, createdEntity, action.position()), player);
    }

    private SpawnEntity ownerEvent(CreatedEntity createdEntity, Position position) {
        return new SpawnEntity(
                createdEntity.entity(),
                position,
                createdEntity.visibilityChange(),
                createdEntity.claimChange()
        );
    }

    private SpawnEntity otherEvent(
            PlayerID player, CreatedEntity createdEntity, Position position
    ) {
        return new SpawnEntity(
                createdEntity.entity(),
                position,
                VisibilityChange.empty(),
                state.maskedFor(player, createdEntity.claimChange())
        );
    }


}
