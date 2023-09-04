package io.game.world.controller.states;

import core.model.EntityID;
import core.model.Position;
import core.terrain.events.SetTerrain;
import core.terrain.model.TerrainType;
import io.animation.Finishable;
import io.game.world.controller.CommonState;
import io.game.world.controller.WorldState;
import mudgame.controls.events.MoveEntityAlongPath;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
                .map(move -> move.destination())
                .flatMap(Optional::stream)
                .toList();

        onFinish(
                state.map().moveAlongPath(event.entityID(), path),
                () -> state.animatedEvents().remove(event)
        );
        nextEvent();
    }

    @Override
    public void onSetTerrain(SetTerrain event) {
        state.animatedEvents().add(event);
        var fogAdded = event.positions().stream()
                .filter(spt -> spt.terrainType() == TerrainType.UNKNOWN &&
                               state.terrain().terrainAt(spt.position()) != TerrainType.UNKNOWN)
                .map(spt -> state.map().addFog(spt.position()));
        var fogRemoved = event.positions().stream()
                .filter(spt -> spt.terrainType() != TerrainType.UNKNOWN &&
                               state.terrain().terrainAt(spt.position()) == TerrainType.UNKNOWN)
                .map(spt -> state.map().removeFog(spt.position()));

        onFinish(
                Finishable.all(Stream.concat(fogAdded, fogRemoved).toList()),
                () -> state.animatedEvents().remove(event)
        );
        nextEvent();
    }
}
