package io.game.world.controller.states;

import core.entities.events.MoveEntity;
import core.model.EntityID;
import core.model.Position;
import core.terrain.events.SetTerrain;
import core.terrain.model.TerrainType;
import io.animation.Finishable;
import io.game.world.arrow.Arrow;
import io.game.world.controller.CommonState;
import io.game.world.controller.WorldState;

import java.util.List;
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
    public void onMoveEntity(MoveEntity event) {
        state.animatedEvents().add(event);
        List<Position> path;
        if (state.pathfinder().isReachable(event.entityID(), event.destination()))
            path = state.pathfinder().findPath(event.entityID(), event.destination());
        else
            path = Arrow.pathBetween(state.entities().entityPosition(event.entityID()), event.destination());
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
