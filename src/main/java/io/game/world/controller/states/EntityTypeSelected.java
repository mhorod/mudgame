package io.game.world.controller.states;

import core.entities.model.EntityType;
import core.model.EntityID;
import core.model.Position;
import io.game.world.controller.CommonState;
import io.game.world.controller.WorldState;
import mudgame.controls.events.*;

import java.util.List;

public class EntityTypeSelected extends WorldState {
    private final EntityType type;

    public EntityTypeSelected(CommonState state, EntityType type) {
        super(state);
        this.type = type;
        state.map().setPath(List.of());
        state.map().setHighlightedTiles(state.spawnManager().allowedSpawnPositions(type));
        state.hud().clear();
        state.hud().setPressed(type);
    }

    @Override
    public void onTileClick(Position position) {
        if (state.spawnManager().allowedSpawnPositions(type).contains(position))
            state.controls().createEntity(type, position);
        change(new Normal(state));
    }

    @Override
    public void onEntityClick(EntityID entity) {
        if (!entityAnimated(entity)) {
            if (hasAnyMoves(entity)) {
                state.map().pickUp(entity);
                change(new UnitSelected(state, entity));
            } else state.map().refuse(entity);
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
        if (type == this.type)
            change(new Normal(state));
        else
            change(new EntityTypeSelected(state, type));
    }

    @Override
    public void onEndTurn() {
        state.hud().setEndTurnEnabled(false);
        state.controls().completeTurn();
        change(new Normal(state));
    }
}
