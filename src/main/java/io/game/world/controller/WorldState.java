package io.game.world.controller;

import core.model.EntityID;
import core.model.Position;
import io.animation.Finishable;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.SpawnEntity;
import mudgame.controls.events.VisibilityChange;
import mudgame.controls.events.VisibilityChange.HidePosition;
import mudgame.controls.events.VisibilityChange.ShowPosition;

import java.util.List;
import java.util.Optional;

public abstract class WorldState implements WorldBehavior {
    private WorldController controller;
    protected final CommonState state;

    protected WorldState(CommonState state) {
        this.state = state;
    }

    void init(WorldController controller) {
        this.controller = controller;
    }

    protected void change(WorldState state) {
        this.controller.setState(state);
    }

    protected void nextEvent() {
        this.state.controls().nextEvent();
    }

    protected void onFinish(Finishable finishable, Runnable f) {
        this.controller.onFinish(finishable, f);
    }

    protected boolean entityAnimated(EntityID entity) {
        return state.animatedEvents().stream().anyMatch(event -> {
            if (!(event instanceof MoveEntityAlongPath))
                return false;
            return ((MoveEntityAlongPath) event).entityID() == entity;
        });
    }

    @Override
    public void onSpawnEntity(SpawnEntity event) {
        state.map().createEntity(event.position(), event.entity());
        changeVisibility(event.visibilityChange());
        nextEvent();
    }

    protected void changeVisibility(VisibilityChange event) {
        event.positions().stream()
                .filter(HidePosition.class::isInstance)
                .forEach(p -> state.map().hideIn(0, p.position()));

        event.positions().stream()
                .filter(ShowPosition.class::isInstance)
                .forEach(p -> state.map().showIn(0, (ShowPosition) p));

    }

    protected void moveEntity(MoveEntityAlongPath event) {
        state.animatedEvents().add(event);
        List<Optional<Position>> path = event.moves()
                .stream()
                .map(MoveEntityAlongPath.SingleMove::destination)
                .toList();

        for (int i = 0; i < event.moves().size(); i++) {
            var move = event.moves().get(i);
            float time = 0.3f + i / 3f;
            move.visibilityChange().positions().forEach(change -> {
                if (change instanceof ShowPosition showPosition)
                    state.map().showIn(time, showPosition);
                if (change instanceof HidePosition)
                    state.map().hideIn(time, change.position());
            });
        }


        onFinish(
                state.map().moveAlongPath(event.entityID(), path),
                () -> state.animatedEvents().remove(event)
        );
    }

    @Override
    public void onRemoveEntity(RemoveEntity event) {
        state.map()
                .removeEntity(state.entities().entityPosition(event.entityID()), event.entityID());
        nextEvent();
    }
}
