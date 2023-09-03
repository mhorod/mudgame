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

import java.util.stream.Stream;

public class UnitSelected extends WorldState {
    private final EntityID selectedUnit;

    public UnitSelected(CommonState state, EntityID unit) {
        super(state);
        this.selectedUnit = unit;
    }

    @Override
    public void onTileClick(Position position) {
        state.controls().moveEntity(selectedUnit, position);
        state.map().putDown(selectedUnit);
        change(new Normal(state));
    }

    @Override
    public void onEntityClick(EntityID entity) {
        if (entityAnimated(entity)) return;
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
        state.map().setPath(Arrow.pathBetween(
                state.entities().entityPosition(selectedUnit),
                position
        ));
    }

    @Override
    public void onEntityHover(EntityID entity) {

    }

    @Override
    public void onMoveEntity(MoveEntity event) {
        state.animatedEvents().add(event);
        onFinish(state.map().moveAlongPath(
                        event.entityID(),
                        Arrow.pathBetween(state.entities().entityPosition(event.entityID()), event.destination())
                ),
                () -> state.animatedEvents().remove(event));
        if (event.entityID().equals(selectedUnit))
            change(new Normal(state));
        nextEvent();
    }

    @Override
    public void onSetTerrain(SetTerrain event) {
        state.animatedEvents().add(event);
        var fogAdded = event.positions().stream()
                .filter(spt -> spt.terrainType() == TerrainType.UNKNOWN && state.terrain().terrainAt(spt.position()) != TerrainType.UNKNOWN)
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
