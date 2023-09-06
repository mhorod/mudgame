package core.fogofwar;

import core.entities.components.Component;
import core.entities.components.ComponentVisitor;
import core.entities.components.Vision;
import core.entities.model.Entity;
import core.model.EntityID;
import core.model.PlayerID;
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

import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public final class PlayerFogOfWar implements PlayerFogOfWarView, Serializable {

    private static final class VisionVisitor implements ComponentVisitor<Integer> {
        @Override
        public Integer visit(Vision vision) {
            return vision.range();
        }
    }

    private record VisionArea(Position center, Integer range) implements Serializable {
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
    private static class EntityHasNoVision extends RuntimeException {
    }

    private static final VisionVisitor visionVisitor = new VisionVisitor();

    public record PositionVisibility(Position position, boolean isVisible) { }


    private final PlayerID playerID;
    private final Map<Position, Integer> visionCount = new HashMap<>();
    private final Map<EntityID, VisionArea> entityVision = new HashMap<>();


    public boolean isVisible(Position position) {
        return visionCount.getOrDefault(position, 0) > 0;
    }

    public List<Position> visiblePositions() {
        return visionCount.keySet()
                .stream()
                .filter(this::isVisible)
                .toList();
    }

    public PlayerID playerID() { return playerID; }

    public Set<PositionVisibility> placeEntity(Entity entity, Position position) {
        if (!entity.owner().equals(playerID) || !hasVision(entity))
            return Set.of();

        VisionArea area = new VisionArea(position, getVisionRange(entity));
        Set<Position> changed = addVisionArea(area);
        entityVision.put(entity.id(), area);
        return visibilities(changed);
    }

    public Set<PositionVisibility> removeEntity(EntityID entityID) {
        if (!entityVision.containsKey(entityID))
            return Set.of();

        VisionArea area = entityVision.get(entityID);
        Set<Position> changed = removeVisionArea(area);
        entityVision.remove(entityID);
        return visibilities(changed);
    }

    private Set<PositionVisibility> visibilities(Set<Position> changed) {
        return changed.stream()
                .map(p -> new PositionVisibility(p, isVisible(p)))
                .collect(toSet());
    }

    public Set<PositionVisibility> moveEntity(EntityID entityID, Position destination) {
        if (!entityVision.containsKey(entityID))
            return Set.of();

        VisionArea currentArea = entityVision.get(entityID);
        VisionArea newArea = new VisionArea(destination, currentArea.range());
        entityVision.put(entityID, newArea);

        Set<Position> removed = removeVisionArea(currentArea);
        Set<Position> added = addVisionArea(newArea);

        return visibilities(xor(removed, added));
    }

    private <T> Set<T> xor(Set<T> s1, Set<T> s2) {
        Set<T> union = new HashSet<>();
        union.addAll(s1);
        union.addAll(s2);

        Set<T> intersection = new HashSet<>(s1);
        intersection.retainAll(s2);

        union.removeAll(intersection);
        return union;
    }

    private boolean hasVision(Entity e) {
        return e.components()
                .stream()
                .anyMatch(c -> c.accept(visionVisitor) != null);
    }

    private Set<Position> addVisionArea(VisionArea area) {
        Set<Position> changed = new HashSet<>();
        for (Position position : area.positions()) {
            if (visionCount.getOrDefault(position, 0) == 0)
                changed.add(position);
            visionCount.put(position, visionCount.getOrDefault(position, 0) + 1);
        }
        return changed;
    }

    private Set<Position> removeVisionArea(VisionArea area) {
        Set<Position> changed = new HashSet<>();
        for (Position position : area.positions()) {
            if (visionCount.get(position) == 1)
                changed.add(position);
            visionCount.put(position, visionCount.get(position) - 1);
        }
        return changed;
    }

    private Integer getVisionRange(Entity e) {
        for (Component c : e.components()) {
            Integer viewRange = c.accept(visionVisitor);
            if (viewRange != null)
                return viewRange;
        }
        throw new EntityHasNoVision();
    }

}
