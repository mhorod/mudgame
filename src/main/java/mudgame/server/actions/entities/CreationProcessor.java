package mudgame.server.actions.entities;

import core.claiming.ClaimChange;
import core.model.PlayerID;
import core.model.Position;
import core.resources.Resources;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.events.ChargeResources;
import mudgame.controls.events.ClaimChanges;
import mudgame.controls.events.SpawnEntity;
import mudgame.controls.events.VisibilityChange;
import mudgame.server.actions.EventSender;
import mudgame.server.internal.CreatedEntity;
import mudgame.server.internal.InteractiveState;

import java.util.List;

/**
 * Creates new entities and sends the events
 */
final class CreationProcessor {
    private final InteractiveState state;
    private final EventSender sender;

    public CreationProcessor(InteractiveState state, EventSender sender) {
        this.state = state;
        this.sender = sender;
    }

    void createEntity(CreateEntity a) {
        CreatedEntity createdEntity = state.createEntity(a.type(), a.owner(), a.position());

        state.players().forEach(p -> sendTo(p, a, createdEntity));
    }

    private void sendTo(
            PlayerID player, CreateEntity action, CreatedEntity createdEntity
    ) {
        if (player.equals(action.owner())) {
            sender.send(ownerEvent(createdEntity, action.position()), player);
            sender.send(chargeResources(createdEntity), player);
        } else if (state.playerSees(player, action.position())) {
            sender.send(otherEvent(player, createdEntity, action.position()), player);
        } else {
            ClaimChange claimChange = createdEntity.claimChange()
                    .masked(state.playerFow(player), state.terrain());
            if (!claimChange.isEmpty())
                sender.send(new ClaimChanges(List.of(claimChange)), player);
        }
    }

    private ChargeResources chargeResources(CreatedEntity createdEntity) {
        return new ChargeResources(createdEntity.entity().getCost().orElse(Resources.empty()));
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
