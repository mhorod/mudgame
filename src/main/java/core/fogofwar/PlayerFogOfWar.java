package core.fogofwar;

import core.entities.components.Component;
import core.entities.components.ComponentVisitor;
import core.entities.components.Vision;
import core.entities.model.Entity;
import core.model.EntityID;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public final class PlayerFogOfWar implements Serializable {
    private static final class VisionVisitor implements ComponentVisitor<Integer> {
        @Override
        public Integer visit(Vision vision) {
            return vision.range();
        }
    }

    private record VisionArea(Position center, Integer range) {
        List<Position> positions() {
            List<Position> result = new ArrayList<>();
            for (int dx = -range; dx <= range; dx++)
                for (int dy = -range; dy <= range; dy++) {
                    result.add(new Position(center.x() + dx, center.y() + dy));
                }
            return result;
        }
    }

    @StandardException
    private static class EntityHasNoVision extends RuntimeException { }

    private static final VisionVisitor visionVisitor = new VisionVisitor();


    private final Map<Position, Integer> visionCount = new HashMap<>();
    private final Map<EntityID, VisionArea> entityVision = new HashMap<>();

    public boolean isVisible(Position position) {
        return visionCount.get(position) > 0;
    }

    Set<Position> placeEntity(Entity entity, Position position) {
        if (!hasVision(entity))
            return Set.of();

        VisionArea area = new VisionArea(position, getVisionRange(entity));
        Set<Position> changed = addVisionArea(area);
        entityVision.put(entity.id(), area);
        return changed;
    }

    Set<Position> removeEntity(EntityID entityID) {
        if (!entityVision.containsKey(entityID))
            return Set.of();

        VisionArea area = entityVision.get(entityID);
        Set<Position> changed = removeVisionArea(area);
        entityVision.remove(entityID);
        return changed;
    }

    Set<Position> moveEntity(EntityID entityID, Position destination) {
        if (!entityVision.containsKey(entityID))
            return Set.of();

        VisionArea currentArea = entityVision.get(entityID);
        VisionArea newArea = new VisionArea(destination, currentArea.range());
        entityVision.put(entityID, newArea);

        Set<Position> removed = removeVisionArea(currentArea);
        Set<Position> added = addVisionArea(newArea);
        Set<Position> common = new HashSet<>(removed);
        added.addAll(removed);
        added.removeAll(common);
        return added;
    }

    private boolean hasVision(Entity e) {
        return e.data()
                .components()
                .stream()
                .anyMatch(c -> c.accept(visionVisitor) != null);
    }

    private Set<Position> addVisionArea(VisionArea area) {
        for (Position position : area.positions())
            setCount(position, visionCount.getOrDefault(position, 0) + 1);
        return new HashSet<>(area.positions());
    }

    private Set<Position> removeVisionArea(VisionArea area) {
        for (Position position : area.positions())
            setCount(position, visionCount.get(position) - 1);
        return new HashSet<>(area.positions());
    }

    private void setCount(Position position, Integer count) {
        visionCount.put(position, count);
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
