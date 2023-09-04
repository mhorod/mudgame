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
        if (event.entityID().equals(selectedUnit))
            change(new Normal(state));
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
                .filter(spt -> spt.terrainType() != TerrainType.UNKNOWN
                               && state.terrain().terrainAt(spt.position()) == TerrainType.UNKNOWN
                ).map(spt -> state.map().removeFog(spt.position()));

        onFinish(
                Finishable.all(Stream.concat(fogAdded, fogRemoved).toList()),
                () -> state.animatedEvents().remove(event)
        );
        nextEvent();
    }
}
