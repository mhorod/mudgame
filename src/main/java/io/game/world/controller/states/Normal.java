package io.game.world.controller.states;

import core.model.EntityID;
import core.model.Position;
import io.game.world.controller.CommonState;
import io.game.world.controller.WorldState;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.VisibilityChange;

import java.util.List;

import static core.entities.model.EntityType.PAWN;

public class Normal extends WorldState {

    public Normal(CommonState state) {
        super(state);
        state.map().setPath(List.of());
        state.map().setHighlightedTiles(state.spawnManager().allowedSpawnPositions(PAWN));
    }

    @Override
    public void onTileClick(Position position) {
        state.controls().createEntity(position);
    }

    @Override
    public void onEntityClick(EntityID entity) {
        if (!entityAnimated(entity)) {
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
        moveEntity(event);
        nextEvent();
    }

    @Override
    public void onVisibilityChange(VisibilityChange event) {
        changeVisibility(event);
        nextEvent();
    }
}
