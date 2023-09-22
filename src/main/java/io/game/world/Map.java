package io.game.world;

import core.claiming.ClaimedAreaView;
import core.entities.EntityBoardView;
import core.model.EntityID;
import core.model.Position;
import core.terrain.TerrainView;
import core.terrain.model.TerrainType;
import io.animation.Animation;
import io.animation.Finishable;
import io.game.Camera;
import io.game.WorldPosition;
import io.game.world.arrow.Arrow;
import io.game.world.entity.AnimatedEntity;
import io.game.world.entity.Entity;
import io.game.world.event_animations.*;
import io.game.world.tile.AnimatedTile;
import io.game.world.tile.AttackMarker;
import io.game.world.tile.Fog;
import io.model.engine.Color;
import io.model.engine.TextureBank;
import mudgame.controls.events.*;

import java.util.*;

public class Map implements Animation {
    HashMap<EntityID, AnimatedEntity> entityAnimations = new HashMap<>();
    private final TerrainView terrain;
    private final EntityBoardView entities;
    private final ClaimedAreaView claims;
    private final ArrayList<Position> path = new ArrayList<>();
    private Set<Position> highlightedTiles = null;
    private Set<Position> attackMarkers = null;
    HashMap<Position, AnimatedTile> animatedTiles = new HashMap<>();
    private Animation mapAnimation;

    public Map(TerrainView terrain, EntityBoardView entities, ClaimedAreaView claims) {
        this.terrain = terrain;
        this.entities = entities;
        this.claims = claims;
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

    public void setAttackMarkers(List<Position> positions) {
        if (positions == null)
            attackMarkers = null;
        else
            attackMarkers = new HashSet<>(positions);
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

    public Set<Position> getAnimatedTiles() {
        return animatedTiles.keySet();
    }

    public AnimatedTile tileFromPosition(Position pos) {
        if (animatedTiles.containsKey(pos))
            return animatedTiles.get(pos);
        var color = claims.owner(pos).map(Color::fromPlayerId).orElse(Color.WHITE);
        var tile = new AnimatedTile(pos, terrain.terrainAt(pos), entities.entitiesAt(pos), color, false);
        animatedTiles.put(pos, tile);
        return tile;
    }


    public MapView getView(TextureBank textureBank, Camera camera) {
        ArrayList<WorldEntity> entities = new ArrayList<>();
        camera.forAllVisibleTiles(
                pos -> entities.addAll(tileFromPosition(pos).getEntities(getAnimatedEntities(), highlightedTiles, this::fog))
        );
        if (attackMarkers != null)
            entities.addAll(attackMarkers.stream().map(AttackMarker::new).toList());
        entities.addAll(Arrow.fromPositions(path));
        entities.addAll(entityAnimations.values().stream().flatMap(
                animation -> animation.getEntity().withShadow().stream()
        ).toList());
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
        entityAnimations.values().forEach(animation -> animation.update(deltaTime));
        if (mapAnimation != null) {
            mapAnimation.update(deltaTime);
            if (mapAnimation.finished())
                mapAnimation = null;
        }
        animatedTiles.values().forEach(animation -> animation.update(deltaTime));

        animatedTiles.entrySet().stream()
                .filter((entry) -> entry.getValue().finished())
                .toList()
                .forEach(entry -> animatedTiles.remove(entry.getKey()));
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

    public Finishable animate(KillEntity event) {
        return mapAnimation = new KillEntityAnimation(this, event);
    }

    public Finishable animate(SpawnEntity event) {
        return mapAnimation = new SpawnEntityAnimation(this, event);
    }

    public Finishable animate(AttackEntityEvent event) {
        return mapAnimation = new AttackEntityAnimation(this, event,
                entities.entityPosition(event.attacker()),
                entities.entityPosition(event.attacked())
        );
    }

    @Override
    public boolean finished() {
        return false;
    }

    public Finishable animate(RemoveEntity event) {
        return mapAnimation = new RemoveEntityAnimation(this, event);
    }
}
