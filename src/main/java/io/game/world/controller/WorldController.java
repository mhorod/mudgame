package io.game.world.controller;

import core.entities.EntityBoard;
import core.entities.events.HideEntity;
import core.entities.events.MoveEntity;
import core.entities.events.RemoveEntity;
import core.entities.events.ShowEntity;
import core.entities.events.SpawnEntity;
import core.model.EntityID;
import core.model.Position;
import core.terrain.Terrain;
import core.terrain.events.SetTerrain;
import io.animation.Finishable;
import io.animation.FutureExecutor;
import io.game.world.Map;
import io.game.world.controller.states.Normal;

import java.util.HashSet;

public class WorldController implements WorldBehavior {
    private WorldState state;
    private final FutureExecutor executor = new FutureExecutor();

    public WorldController(Map map, EntityBoard entities, Terrain terrain, Controls controls) {
        state = new Normal(new CommonState(map, terrain, entities, controls, new HashSet<>()));
        state.init(this);
    }

    void setState(WorldState state) {
        state.init(this);
        this.state = state;
    }

    void onFinish(Finishable finishable, Runnable f) {
        executor.onFinish(finishable, f);
    }

    public void update() {
        executor.update();
    }

    @Override
    public void onTileClick(Position position) {
        state.onTileClick(position);
    }

    @Override
    public void onEntityClick(EntityID entity) {
        state.onEntityClick(entity);
    }

    @Override
    public void onTileHover(Position position) {
        state.onTileHover(position);
    }

    @Override
    public void onEntityHover(EntityID entity) {
        state.onEntityHover(entity);
    }

    @Override
    public void onMoveEntity(MoveEntity event) {
        state.onMoveEntity(event);
    }

    @Override
    public void onSetTerrain(SetTerrain event) {
        state.onSetTerrain(event);
    }

    @Override
    public void onPlaceEntity(SpawnEntity event) {
        state.onPlaceEntity(event);
    }

    @Override
    public void onRemoveEntity(RemoveEntity event) {
        state.onRemoveEntity(event);
    }

    @Override
    public void onShowEntity(ShowEntity event) {
        state.onShowEntity(event);
    }

    @Override
    public void onHideEntity(HideEntity event) {
        state.onHideEntity(event);
    }
}
