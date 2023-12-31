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
        state.map().setAttackMarkers(null);
        state.hud().clear();
    }

    @Override
    public void onTileClick(Position position) {
    }

    @Override
    public void onEntityClick(EntityID entity) {
        if (entityAnimated(entity))
            return;
        if (hasAnyMoves(entity)) {
            state.map().pickUp(entity);
            change(new UnitSelected(state, entity));
        } else state.map().refuse(entity);
    }

    @Override
    public void onTileHover(Position position) {
        state.hud().hideEntityInfo();
    }

    @Override
    public void onEntityHover(EntityID entityID) {
        var entity = state.entities().findEntityByID(entityID);
        if (entity != null)
            state.hud().showEntityInfo(entity);
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
    public void onAttackEntity(AttackEntityEvent e) {

    }

    @Override
    public void onKillEntity(KillEntity e) {

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
