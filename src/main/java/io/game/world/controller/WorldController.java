package io.game.world.controller;

import core.entities.EntityBoardView;
import core.model.EntityID;
import core.model.Position;
import core.pathfinder.Pathfinder;
import core.spawning.PlayerSpawnManager;
import core.terrain.TerrainView;
import io.animation.Finishable;
import io.animation.FutureExecutor;
import io.game.world.Map;
import io.game.world.controller.states.Normal;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.SpawnEntity;
import mudgame.controls.events.VisibilityChange;

import java.util.HashSet;

public class WorldController implements WorldBehavior {
    private WorldState state;
    private final FutureExecutor executor = new FutureExecutor();

    public WorldController(
            Map map,
            EntityBoardView entities,
            TerrainView terrain,
            Pathfinder pathfinder,
            PlayerSpawnManager spawnManager,
            Controls controls
    ) {
        state = new Normal(
                new CommonState(map, terrain, entities, pathfinder, spawnManager, controls,
                                new HashSet<>()));
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
    public void onVisibilityChange(VisibilityChange event) {
        state.onVisibilityChange(event);
    }

    @Override
    public void onSpawnEntity(SpawnEntity event) {
        state.onSpawnEntity(event);
    }

    @Override
    public void onRemoveEntity(RemoveEntity event) {
        state.onRemoveEntity(event);
    }

    @Override
    public void onMoveEntityAlongPath(MoveEntityAlongPath e) {
        state.onMoveEntityAlongPath(e);
    }
}
