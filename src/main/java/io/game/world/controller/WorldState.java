package io.game.world.controller;

import core.entities.events.MoveEntity;
import core.entities.events.PlaceEntity;
import core.entities.events.RemoveEntity;
import core.model.EntityID;
import io.animation.Finishable;

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
            if (!(event instanceof MoveEntity)) return false;
            return ((MoveEntity) event).entityID() == entity;
        });
    }

    @Override
    public void onPlaceEntity(PlaceEntity event) {
        state.map().createUnit(event.position(), event.entity().id());
    }

    @Override
    public void onRemoveEntity(RemoveEntity event) {
        state.map().removeUnit(state.entities().entityPosition(event.entityID()), event.entityID());
    }
}
