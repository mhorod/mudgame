package io.game.world.controller.states;

import core.entities.model.EntityType;
import core.model.EntityID;
import core.model.Position;
import io.game.world.controller.CommonState;
import io.game.world.controller.WorldState;
import mudgame.controls.events.*;

import java.util.List;

public class Normal extends WorldState {

    public Normal(CommonState state) {
        super(state);
        state.map().setPath(List.of());
        state.map().setHighlightedTiles(null);
        state.hud().clear();
    }

    @Override
    public void onTileClick(Position position) {
    }

    @Override
    public void onEntityClick(EntityID entity) {
        if (entityAnimated(entity))
            return;
        if (state.pathfinder().reachablePositions(entity).getPositions().isEmpty())
            state.map().refuse(entity);
        else {
            state.map().pickUp(entity);
            change(new UnitSelected(state, entity));
        }
    }

    @Override
    public void onTileHover(Position position) {

    }

    @Override
    public void onEntityHover(EntityID entity) {

    }

    @Override
    public void onMoveEntityAlongPath(MoveEntityAlongPath event) {
    }

    @Override
    public void onNextTurn(NextTurn e) {
        if (state.myID().equals(e.currentPlayer()))
            state.hud().setEndTurnEnabled(true);
    }

    @Override
    public void onVisibilityChange(VisibilityChange event) {
    }

    @Override
    public void onSpawnEntity(SpawnEntity event) {

    }

    @Override
    public void onRemoveEntity(RemoveEntity event) {

    }

    @Override
    public void onEntityTypeSelected(EntityType type) {
        change(new EntityTypeSelected(state, type));
    }

    @Override
    public void onEndTurn() {
        state.hud().setEndTurnEnabled(false);
        state.controls().completeTurn();
    }
}
