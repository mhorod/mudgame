package io.game.world.controller;

import core.entities.EntityBoardView;
import core.entities.model.EntityType;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.pathfinder.Pathfinder;
import core.spawning.PlayerSpawnManager;
import core.terrain.TerrainView;
import io.animation.Finishable;
import io.animation.FutureExecutor;
import io.game.ui.HUD;
import io.game.world.Map;
import io.game.world.controller.states.Normal;
import mudgame.client.PlayerAttackManager;
import mudgame.controls.Controls;
import mudgame.controls.events.*;

import java.util.HashSet;

public class WorldController implements WorldBehavior {
    private WorldState state;
    private final FutureExecutor executor = new FutureExecutor();

    public WorldController(
            Map map,
            HUD hud,
            PlayerID myID,
            EntityBoardView entities,
            TerrainView terrain,
            Pathfinder pathfinder,
            PlayerSpawnManager spawnManager,
            PlayerAttackManager attackManager,
            Controls controls
    ) {
        state = new Normal(
                new CommonState(map, hud, myID, terrain, entities, pathfinder, spawnManager, attackManager, controls,
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

    @Override
    public void onNextTurn(NextTurn e) {
        state.onNextTurn(e);
    }

    @Override
    public void onAttackEntity(AttackEntityEvent e) {
        state.onAttackEntity(e);
    }

    @Override
    public void onEntityTypeSelected(EntityType type) {
        state.onEntityTypeSelected(type);
    }

    @Override
    public void onEndTurn() {
        state.onEndTurn();
    }
}
