package io.game.world;

import core.entities.EntityBoardView;
import core.model.EntityID;
import core.model.Position;
import core.terrain.TerrainView;
import core.terrain.model.TerrainType;
import io.animation.Animation;
import io.animation.Finishable;
import io.game.Camera;
import io.game.MapView;
import io.game.WorldPosition;
import io.game.world.arrow.Arrow;
import io.game.world.entity.AnimatedEntity;
import io.game.world.entity.Entity;
import io.game.world.entity.EntityAnimation;
import io.game.world.entity.WorldEntity;
import io.game.world.event_animations.MoveEntityAlongPathAnimation;
import io.game.world.event_animations.RemoveEntityAnimation;
import io.game.world.event_animations.SpawnEntityAnimation;
import io.game.world.event_animations.VisibilityChangeAnimation;
import io.game.world.tile.AnimatedTile;
import io.game.world.tile.Fog;
import io.model.engine.TextureBank;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.SpawnEntity;
import mudgame.controls.events.VisibilityChange;

import java.util.*;

public class Map implements Animation {
    HashMap<EntityID, AnimatedEntity> entityAnimations = new HashMap<>();
    ArrayList<EntityAnimation> otherAnimations = new ArrayList<>();
    private final TerrainView terrain;
    private final EntityBoardView entities;
    private final ArrayList<Position> path = new ArrayList<>();
    private Set<Position> highlightedTiles = null;
    HashMap<Position, AnimatedTile> tmpTiles = new HashMap<>();
    private Animation mapAnimation;

    public Map(TerrainView terrain, EntityBoardView entities) {
        this.terrain = terrain;
        this.entities = entities;
    }

    public void setPath(List<Position> positions) {
        path.clear();
        path.addAll(positions);
    }

    public void setHighlightedTiles(List<Position> positions) {
        if (positions == null)
            highlightedTiles = null;
        else
            highlightedTiles = new HashSet<>(positions);
    }

    public AnimatedEntity entityFromID(EntityID id) {
        if (entityAnimations.containsKey(id))
            return entityAnimations.get(id);
        var entity = new AnimatedEntity(new Entity(WorldPosition.from(entities.entityPosition(id)),
                entities.findEntityByID(id)));
        entityAnimations.put(id, entity);
        return entity;
    }

    public Set<EntityID> getAnimatedEntities() {
        return entityAnimations.keySet();
    }

    public AnimatedTile tileFromPosition(Position pos) {
        if (tmpTiles.containsKey(pos))
            return tmpTiles.get(pos);
        var tile = new AnimatedTile(pos, terrain.terrainAt(pos), entities.entitiesAt(pos), false);
        tmpTiles.put(pos, tile);
        return tile;
    }


    public MapView getView(TextureBank textureBank, Camera camera) {
        ArrayList<WorldEntity> entities = new ArrayList<>();
        camera.forAllVisibleTiles(
                pos -> entities.addAll(tileFromPosition(pos).getEntities(getAnimatedEntities(), highlightedTiles, this::fog))
        );
        entities.addAll(Arrow.fromPositions(path));
        entities.addAll(entityAnimations.values().stream().flatMap(
                animation -> animation.getEntity().withShadow().stream()
        ).toList());
        entities.addAll(otherAnimations.stream().map(EntityAnimation::getEntity).toList());
        return new MapView(entities, camera, textureBank);
    }

    public Fog fog(Position pos) {
        var left = terrain.terrainAt(new Position(pos.x(), pos.y() + 1)) == TerrainType.VOID;
        var right = terrain.terrainAt(new Position(pos.x() + 1, pos.y())) == TerrainType.VOID;
        if (left && right)
            return new Fog(pos, WorldTexture.FOG_TALL);
        if (left)
            return new Fog(pos, WorldTexture.FOG_LEFT);
        if (right)
            return new Fog(pos, WorldTexture.FOG_RIGHT);
        return new Fog(pos, WorldTexture.FOG);
    }

    @Override
    public void update(float deltaTime) {
        otherAnimations.forEach(animation -> animation.update(deltaTime));
        entityAnimations.values().forEach(animation -> animation.update(deltaTime));
        if (mapAnimation != null) {
            mapAnimation.update(deltaTime);
            if (mapAnimation.finished())
                mapAnimation = null;
        }
        tmpTiles.values().forEach(animation -> animation.update(deltaTime));

        otherAnimations.removeIf(Animation::finished);
        tmpTiles.entrySet().stream()
                .filter((entry) -> entry.getValue().finished())
                .toList()
                .forEach(entry -> tmpTiles.remove(entry.getKey()));
        entityAnimations.entrySet().stream()
                .filter((entry) -> entry.getValue().finished())
                .toList()
                .forEach(entry -> entityAnimations.remove(entry.getKey()));
    }

    public void pickUp(EntityID entity) {
        entityFromID(entity).pickUp();
    }

    public void refuse(EntityID entity) {
        entityFromID(entity).refuse();
    }

    public void putDown(EntityID entity) {
        entityFromID(entity).putDown();
    }

    public AnimatedEntity createEntity(Position position, core.entities.model.Entity entity) {
        var animatedEntity = new AnimatedEntity(new Entity(WorldPosition.from(position), entity));
        entityAnimations.put(entity.id(), animatedEntity);
        return animatedEntity;
    }

    public Finishable animate(MoveEntityAlongPath event) {
        return mapAnimation = new MoveEntityAlongPathAnimation(this, event);
    }

    public Finishable animate(VisibilityChange event) {
        return mapAnimation = new VisibilityChangeAnimation(this, event);
    }

    public Finishable animate(SpawnEntity event) {
        return mapAnimation = new SpawnEntityAnimation(this, event);
    }

    @Override
    public boolean finished() {
        return false;
    }

    public Finishable animate(RemoveEntity event) {
        return mapAnimation = new RemoveEntityAnimation(this, event);
    }
}
