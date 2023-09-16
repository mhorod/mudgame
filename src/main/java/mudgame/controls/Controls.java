package mudgame.controls;

import core.entities.model.EntityType;
import mudgame.controls.actions.Action;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.actions.CompleteTurn;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.actions.MoveEntity;

import java.util.function.Consumer;

import static core.entities.model.EntityType.MARSH_WIGGLE;

@RequiredArgsConstructor
public class Controls {
    private final PlayerID player;
    private final Consumer<Action> actionConsumer;

    public void moveEntity(EntityID id, Position destination) {
        actionConsumer.accept(new MoveEntity(id, destination));
    }

    public void createEntity(Position position) {
        actionConsumer.accept(new CreateEntity(MARSH_WIGGLE, player, position));
    }

    public void createEntity(EntityType type, Position position) {
        actionConsumer.accept(new CreateEntity(type, player, position));
    }

    public void completeTurn() {
        actionConsumer.accept(new CompleteTurn());
    }

    public void attackEntity(EntityID attacker, EntityID attacked) {
        actionConsumer.accept(new AttackEntityAction(attacker, attacked));
    }
}
