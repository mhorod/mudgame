package io.game.world.controller;

import core.entities.EntityBoard;
import core.model.EntityID;
import core.model.Position;
import core.pathfinder.Pathfinder;
import core.terrain.Terrain;
import core.terrain.events.SetTerrain;
import io.animation.Finishable;
import io.animation.FutureExecutor;
import io.game.world.Map;
import io.game.world.controller.states.Normal;
import mudgame.controls.events.HideEntity;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.ShowEntity;
import mudgame.controls.events.SpawnEntity;

import java.util.HashSet;

public class WorldController implements WorldBehavior {
    private WorldState state;
    private final FutureExecutor executor = new FutureExecutor();

    public WorldController(
            Map map, EntityBoard entities, Terrain terrain, Pathfinder pathfinder, Controls controls
    ) {
        state = new Normal(
                new CommonState(map, terrain, entities, pathfinder, controls, new HashSet<>()));
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

    @Override
    public void onMoveEntityAlongPath(MoveEntityAlongPath e) {
    }
}
