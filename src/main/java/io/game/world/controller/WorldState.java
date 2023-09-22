package io.game.world.controller;

import core.model.EntityID;
import mudgame.controls.events.MoveEntityAlongPath;

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

    protected boolean hasAnyMoves(EntityID entity) {
        if (!state.entities().containsEntity(entity)) return false;
        return !state.pathfinder().reachablePositions(entity).getPositions().isEmpty()
                || !state.attackManager().attackableEntities(entity).isEmpty();
    }

    protected boolean entityAnimated(EntityID entity) {
        return state.animatedEvents().stream().anyMatch(event -> {
            if (!(event instanceof MoveEntityAlongPath))
                return false;
            return ((MoveEntityAlongPath) event).entityID() == entity;
        });
    }
}
