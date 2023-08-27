package core.entities;

import core.entities.components.Component;
import core.entities.components.ComponentVisitor;
import core.entities.components.Vision;
import core.entities.model.Entity;
import core.fogofwar.PlayerFogOfWar;
import core.model.EntityID;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class EntityVisionBoard {
    private static final class VisionVisitor implements ComponentVisitor<Integer> {
        public Integer visit(Vision vision) {
            return vision.range();
        }
    }

    private record VisionArea(Position center, Integer range) { }

    @StandardException
    private static class EntityHasNoVision extends RuntimeException { }

    private static final VisionVisitor visionVisitor = new VisionVisitor();


    private final Map<Position, Integer> visionCount = new HashMap<>();
    private final Map<EntityID, VisionArea> entityVision = new HashMap<>();
    private final PlayerFogOfWar fow;

    public void placeEntity(Entity entity, Position position) {
        if (!hasVision(entity))
            return;

        VisionArea area = new VisionArea(position, getVisionRange(entity));
        addVisionArea(area);
        entityVision.put(entity.id(), area);
    }

    public void removeEntity(EntityID entityID) {
        if (!entityVision.containsKey(entityID))
            return;

        VisionArea area = entityVision.get(entityID);
        removeVisionArea(area);
        entityVision.remove(entityID);
    }

    public void moveEntity(EntityID entityID, Position destination) {
        if (!entityVision.containsKey(entityID))
            return;

        VisionArea currentArea = entityVision.get(entityID);
        VisionArea newArea = new VisionArea(destination, currentArea.range());
        entityVision.put(entityID, newArea);

        removeVisionArea(currentArea);
        addVisionArea(newArea);
    }

    private boolean hasVision(Entity e) {
        return e.data()
                .components()
                .stream()
                .anyMatch(c -> c.accept(visionVisitor) != null);
    }

    private void addVisionArea(VisionArea area) {
        for (int dx = -area.range(); dx <= area.range(); dx++)
            for (int dy = -area.range(); dy <= area.range(); dy++) {
                Position position = new Position(area.center().x() + dx, area.center().y() + dy);
                setCount(position, visionCount.getOrDefault(position, 0) + 1);
            }
    }

    private void removeVisionArea(VisionArea area) {
        for (int dx = -area.range(); dx <= area.range(); dx++)
            for (int dy = -area.range(); dy <= area.range(); dy++) {
                Position position = new Position(area.center().x() + dx, area.center().y() + dy);
                setCount(position, visionCount.get(position) - 1);
            }
    }

    private void setCount(Position position, Integer count) {
        fow.setVisibility(position, count != 0);
    }

    private Integer getVisionRange(Entity e) {
        for (Component c : e.data().components()) {
            Integer viewRange = c.accept(visionVisitor);
            if (viewRange != null)
                return viewRange;
        }
        throw new EntityHasNoVision();
    }

}
