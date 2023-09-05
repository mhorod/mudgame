package io.game.world.tile;


import core.entities.model.Entity;
import core.model.Position;
import io.animation.Animation;
import io.animation.Finishable;
import io.game.WorldPosition;
import io.game.world.WorldTexture;
import io.game.world.entity.Condense;
import io.game.world.entity.Dissipate;
import io.game.world.entity.EntityAnimation;
import io.game.world.entity.WorldEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

public class TileAnimation implements Animation {
    TreeMap<Float, PlannedChange> events = new TreeMap<>();

    private TileKind kind;
    private TileKind actualKind;
    private final Position position;
    private List<Entity> entities;
    private final ArrayList<EntityAnimation> animations = new ArrayList<>();
    private EntityAnimation fogAnimation;
    private float totalTime = 0;

    public TileAnimation(Position position, TileKind kind, List<Entity> entities) {
        this.kind = kind;
        this.actualKind = kind;
        this.entities = entities;
        this.position = position;
    }

    public TileKind getKind() {
        return kind;
    }

    public List<io.game.world.entity.Entity> getEntities() {
        var staticEntities = entities.stream().filter(entity -> animations.stream().noneMatch(
                animation -> ((io.game.world.entity.Entity) animation.getEntity()).getId() == entity.id()
        )).map(
                entity -> new io.game.world.entity.Entity(WorldPosition.from(position), entity)
        );
        var animatedEntities = animations.stream()
                .map(EntityAnimation::getEntity)
                .map(io.game.world.entity.Entity.class::cast);
        return Stream.concat(staticEntities, animatedEntities).toList();
    }

    public List<WorldEntity> otherWorldEntities() {
        return Optional.ofNullable(fogAnimation).stream().map(EntityAnimation::getEntity).toList();
    }

    @Override
    public void update(float deltaTime) {
        totalTime += deltaTime;
        while (!events.isEmpty() && events.firstKey() < totalTime) {
            var event = events.pollFirstEntry().getValue();
            if (kind == TileKind.FOG && event.kind() != TileKind.FOG) {
                fogAnimation = new Dissipate();
                fogAnimation.init(new Tile(position, WorldTexture.FOG));
                kind = event.kind();
            } else if (kind != TileKind.FOG && event.kind() == TileKind.FOG) {
                fogAnimation = new Condense();
                fogAnimation.init(new Tile(position, WorldTexture.FOG));
            }

            animations.addAll(entities.stream().map(entity -> {
                var animation = new Dissipate();
                animation.init(new io.game.world.entity.Entity(WorldPosition.from(position), entity));
                return animation;
            }).toList());
            entities = event.entities();
            animations.addAll(entities.stream().map(entity -> {
                var animation = new Condense();
                animation.init(new io.game.world.entity.Entity(WorldPosition.from(position), entity));
                return animation;
            }).toList());
            actualKind = event.kind();
        }
        animations.forEach(animation -> animation.update(deltaTime));
        animations.removeIf(Finishable::finished);

        Optional.ofNullable(fogAnimation).ifPresent(animation -> animation.update(deltaTime));
        if (fogAnimation != null && fogAnimation.finished()) {
            kind = actualKind;
            fogAnimation = null;
        }
    }

    public void changeIn(float time, PlannedChange change) {
        events.put(totalTime + time, change);
    }

    @Override
    public boolean finished() {
        return animations.isEmpty() && fogAnimation == null && events.isEmpty();
    }
}
