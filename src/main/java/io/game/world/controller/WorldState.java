package io.game.world.controller;

import core.model.EntityID;
import io.animation.Finishable;
import mudgame.controls.events.HideEntity;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.ShowEntity;
import mudgame.controls.events.SpawnEntity;

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
    public void onPlaceEntity(SpawnEntity event) {
        state.map().createEntity(event.position(), event.entity());
        nextEvent();
    }

    @Override
    public void onRemoveEntity(RemoveEntity event) {
        state.map()
                .removeEntity(state.entities().entityPosition(event.entityID()), event.entityID());
        nextEvent();
    }

    @Override
    public void onShowEntity(ShowEntity event) {
        state.map().showEntity(event.position(), event.entity());
        nextEvent();
    }

    @Override
    public void onHideEntity(HideEntity event) {
        state.map().hideEntity(state.entities().entityPosition(event.entityID()), event.entityID());
        nextEvent();
    }
}
