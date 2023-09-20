package io.game.world.controller.states;

import core.model.EntityID;
import core.model.Position;
import io.game.world.controller.CommonState;
import io.game.world.controller.WorldState;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.SpawnEntity;
import mudgame.controls.events.VisibilityChange;

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
        if (entity.equals(selectedUnit)) {
            change(new Normal(state));
        } else {
            state.map().pickUp(entity);
            change(new UnitSelected(state, entity));
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
}
