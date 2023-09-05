package io.game.world.controller.states;

import core.model.EntityID;
import core.model.Position;
import io.game.world.controller.CommonState;
import io.game.world.controller.WorldState;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.MoveEntityAlongPath.SingleMove;
import mudgame.controls.events.VisibilityChange;

import java.util.List;
import java.util.Optional;

public class Normal extends WorldState {

    public Normal(CommonState state) {
        super(state);
        state.map().setPath(List.of());
        state.map().setHighlightedTiles(null);
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
        state.animatedEvents().add(event);
        List<Position> path = event.moves()
                .stream()
                .map(SingleMove::destination)
                .flatMap(Optional::stream)
                .toList();

        onFinish(
                state.map().moveAlongPath(event.entityID(), path),
                () -> state.animatedEvents().remove(event)
        );
        nextEvent();
    }

    @Override
    public void onVisibilityChange(VisibilityChange event) {
        changeVisibility(event);
        nextEvent();
    }
}
