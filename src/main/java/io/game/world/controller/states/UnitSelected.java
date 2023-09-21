package io.game.world.controller.states;

import core.entities.model.EntityType;
import core.model.EntityID;
import core.model.Position;
import io.game.world.controller.CommonState;
import io.game.world.controller.WorldState;
import mudgame.controls.events.*;

public class UnitSelected extends WorldState {
    private final EntityID selectedUnit;

    public UnitSelected(CommonState state, EntityID unit) {
        super(state);
        this.selectedUnit = unit;
        state.map()
                .setHighlightedTiles(state.pathfinder()
                        .reachablePositions(selectedUnit)
                        .getPositions()
                        .stream()
                        .toList());
        state.map().setAttackMarkers(state.attackManager().attackableEntities(selectedUnit).stream().map(
                entity -> state.entities().entityPosition(entity)
        ).toList());
        state.hud().clear();
    }

    @Override
    public void onTileClick(Position position) {
        state.controls().moveEntity(selectedUnit, position);
        state.map().putDown(selectedUnit);
        change(new Normal(state));
    }

    @Override
    public void onEntityClick(EntityID entity) {
        if (entityAnimated(entity))
            return;
        state.map().putDown(selectedUnit);
        if (state.attackManager().attackableEntities(selectedUnit).contains(entity)) {
            state.controls().attackEntity(selectedUnit, entity);
            change(new Normal(state));
            return;
        }
        if (entity.equals(selectedUnit)) {
            change(new Normal(state));
        } else {
            if (hasAnyMoves(entity)) {
                state.map().pickUp(entity);
                change(new UnitSelected(state, entity));
            } else state.map().refuse(entity);
        }
    }

    @Override
    public void onTileHover(Position position) {
        if (!state.pathfinder().isReachable(selectedUnit, position))
            return;
        state.map().setPath(state.pathfinder().findPath(selectedUnit, position));
    }

    @Override
    public void onEntityHover(EntityID entity) {

    }

    @Override
    public void onMoveEntityAlongPath(MoveEntityAlongPath event) {
        if (event.entityID().equals(selectedUnit))
            change(new Normal(state));
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
        if (event.entityID().equals(selectedUnit))
            change(new Normal(state));
    }

    @Override
    public void onEntityTypeSelected(EntityType type) {
        change(new EntityTypeSelected(state, type));
    }

    @Override
    public void onEndTurn() {
        state.controls().completeTurn();
        state.hud().setEndTurnEnabled(false);
        state.map().putDown(selectedUnit);
        change(new Normal(state));
    }
}
