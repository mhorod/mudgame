package io.game.world.tile;

import core.entities.model.Entity;
import core.model.EntityID;
import core.model.Position;
import core.terrain.model.TerrainType;
import io.animation.Animation;
import io.animation.AnimationController;
import io.animation.Finishable;
import io.game.WorldPosition;
import io.game.world.WorldTexture;
import io.game.world.entity.Condense;
import io.game.world.entity.Dissipate;
import io.game.world.entity.EntityAnimation;
import io.game.world.entity.WorldEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AnimatedTile implements Animation {

    private final Position position;
    private TerrainType terrain;
    private List<Entity> entities;
    private boolean covered;

    private boolean existing = false;

    private Finishable coverAnimation;

    private final AnimationController<EntityAnimation> animations = new AnimationController<>();

    public AnimatedTile(Position position, TerrainType type, List<Entity> entities, boolean covered) {
        this.position = position;
        this.terrain = type;
        this.entities = entities;
        this.covered = covered;
    }

    public List<WorldEntity> getEntities(Set<EntityID> animatedEntities, Set<Position> highlightedTiles, FogGenerator generator) {
        if (covered) return List.of(generator.getFog(position));
        var entities = new ArrayList<>(this.entities.stream().filter(
                entity -> !animatedEntities.contains(entity.id())
        ).flatMap(
                entity -> new io.game.world.entity.Entity(WorldPosition.from(position), entity).withShadow().stream()
        ).toList());

        if (terrain == TerrainType.LAND && highlightedTiles != null && !highlightedTiles.contains(position))
            entities.add(new Lowlight(position));

        entities.addAll(animations.getAnimations().stream().map(EntityAnimation::getEntity).toList());

        switch (terrain) {
            case LAND, MOUNTAIN -> entities.add(new Tile(position, WorldTexture.TILE_DARK));
            case WATER -> entities.add(new Tile(position, WorldTexture.TILE_LIGHT));
            case UNKNOWN -> entities.add(generator.getFog(position));
        }
        return entities;
    }

    public List<Entity> getEntities(Set<EntityID> animatedEntities) {
        return entities.stream().filter(entity -> !animatedEntities.contains(entity.id())).toList();
    }

    public void setTerrain(TerrainType terrain) {
        this.terrain = terrain;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public void exist() {
        existing = true;
    }

    public void die() {
        existing = false;
    }

    public Finishable cover(FogGenerator generator) {
        var fogAnimation = new Condense();
        fogAnimation.init(generator.getFog(position));
        animations.addAnimation(fogAnimation);
        coverAnimation = fogAnimation;
        return fogAnimation;
    }

    public Finishable uncover(FogGenerator generator) {
        covered = false;
        var fogAnimation = new Dissipate();
        fogAnimation.init(generator.getFog(position));
        animations.addAnimation(fogAnimation);
        return fogAnimation;
    }

    @Override
    public void update(float deltaTime) {
        animations.update(deltaTime);
        if (coverAnimation != null && coverAnimation.finished()) {
            coverAnimation = null;
            covered = true;
        }
    }

    @Override
    public boolean finished() {
        return !existing && animations.allFinished();
    }

    public Position getPosition() {
        return position;
    }
}
