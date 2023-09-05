package io.game.world;

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
import io.game.world.entity.AnimationChain;
import io.game.world.entity.Condense;
import io.game.world.entity.Dissipate;
import io.game.world.entity.Drop;
import io.game.world.entity.Entity;
import io.game.world.entity.EntityAnimation;
import io.game.world.entity.Exist;
import io.game.world.entity.Hover;
import io.game.world.entity.MoveAlong;
import io.game.world.entity.Raise;
import io.game.world.entity.WorldEntity;
import io.game.world.tile.Tile;
import io.game.world.tile.TileKind;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Map implements Animation {
    HashMap<EntityID, EntityAnimation> entityAnimations = new HashMap<>();
    HashMap<Position, EntityAnimation> tileAnimations = new HashMap<>();
    ArrayList<EntityAnimation> otherAnimations = new ArrayList<>();
    private final TerrainView terrain;
    private final EntityBoardView entities;
    private final ArrayList<Position> path = new ArrayList<>();
    private Collection<Position> highlightedTiles = null;

    public Map(TerrainView terrain, EntityBoardView entities) {
        this.terrain = terrain;
        this.entities = entities;
    }

    public void setPath(List<Position> positions) {
        path.clear();
        path.addAll(positions);
    }

    public void setHighlightedTiles(List<Position> positions) {
        highlightedTiles = positions;
    }

    private void setAnimation(EntityID entityID, EntityAnimation animation) {
        animation.init(entityFromID(entityID));
        entityAnimations.put(entityID, animation);
    }

    private WorldEntity entityFromID(EntityID id) {
        if (entityAnimations.containsKey(id))
            return entityAnimations.get(id).getEntity();
        return new Entity(WorldPosition.from(entities.entityPosition(id)),
                          entities.findEntityByID(id));
    }

    public void objectAt(
            ScreenPosition position, TextureBank textureBank, Camera camera, MapObserver listener
    ) {
        var clickedEntity = entities.allEntities().stream()
                .map(core.entities.model.Entity::id)
                .map(this::entityFromID)
                .filter(entity -> entity.contains(position, textureBank, camera))
                .findFirst();
        if (clickedEntity.isPresent()) {
            listener.onEntity(((Entity) clickedEntity.get()).getId());
            return;
        }
        var tile = camera.getTile(position);
        if (terrain.terrainAt(tile) != TerrainType.VOID)
            listener.onTile(tile);
    }

    public void draw(Canvas canvas, Camera camera) {
        ArrayList<Tile> fogTiles = new ArrayList<>();
        ArrayList<WorldEntity> highlightTiles = new ArrayList<>();
        ArrayList<WorldEntity> entitiesToDraw = new ArrayList<>();
        camera.forAllVisibleTiles(canvas.getAspectRatio(), pos -> {
            if (tileAnimations.containsKey(pos)) {
                tileAnimations.get(pos).getEntity().draw(canvas, camera);
            } else {
                var tile = terrain.terrainAt(pos);
                switch (tile) {
                    case UNKNOWN -> fogTiles.add(new Tile(pos, TileKind.FOG));
                    case WATER -> new Tile(pos, TileKind.TILE_LIGHT).draw(canvas, camera);
                    case LAND -> new Tile(pos, TileKind.TILE_DARK).draw(canvas, camera);
                }
                if (highlightedTiles != null && !highlightedTiles.contains(pos) &&
                    tile == TerrainType.LAND)
                    highlightTiles.add(
                            new WorldEntity(WorldPosition.from(pos), WorldTexture.TILE_HIGHLIGHT,
                                            false)
                    );
            }


            entitiesToDraw.addAll(
                    entities.entitiesAt(pos).stream()
                            .filter(e -> !entityAnimations.containsKey(e.id()))
                            .map(e -> entityFromID(e.id()))
                            .toList()
            );
        });
        highlightTiles.forEach(tile -> tile.draw(canvas, camera));

        Arrow.fromPositions(path).forEach(arrow -> arrow.draw(canvas, camera));

        entitiesToDraw.addAll(
                entityAnimations.values().stream().map(EntityAnimation::getEntity).toList());
        entitiesToDraw.addAll(otherAnimations.stream().map(EntityAnimation::getEntity).toList());
        entitiesToDraw.sort((a, b) -> {
            var valA = a.getPosition().x() + a.getPosition().y();
            var valB = b.getPosition().x() + b.getPosition().y();
            return (int) (100 * (valA - valB));
        });
        entitiesToDraw.forEach(entity -> entity.drawShadow(canvas, camera));
        fogTiles.forEach(fog -> fog.draw(canvas, camera));
        entitiesToDraw.forEach(entity -> entity.draw(canvas, camera));
    }

    @Override
    public void update(float deltaTime) {
        otherAnimations.forEach(animation -> animation.update(deltaTime));
        entityAnimations.values().forEach(animation -> animation.update(deltaTime));
        tileAnimations.values().forEach(animation -> animation.update(deltaTime));

        otherAnimations.removeIf(Animation::finished);
        entityAnimations.entrySet().stream()
                .filter((entry) -> entry.getValue().finished())
                .toList()
                .forEach(entry -> entityAnimations.remove(entry.getKey()));
        tileAnimations.entrySet().stream()
                .filter((entry) -> entry.getValue().finished())
                .toList()
                .forEach(entry -> tileAnimations.remove(entry.getKey()));
    }

    public void pickUp(EntityID entity) {
        setAnimation(entity, new AnimationChain(List.of(new Raise(), new Hover())));
    }

    public void putDown(EntityID entity) {
        setAnimation(entity, new Drop());
    }

    public Finishable removeFog(Position position) {
        var animation = new Dissipate();
        animation.init(new WorldEntity(WorldPosition.from(position), WorldTexture.FOG, false));
        otherAnimations.add(animation);
        return animation;
    }

    public Finishable addFog(Position position) {
        var tileReplacement = new Exist(Condense.TIME);
        tileReplacement.init(
                new WorldEntity(WorldPosition.from(position), WorldTexture.TILE_DARK, false));
        var animation = new Condense();
        animation.init(new WorldEntity(WorldPosition.from(position), WorldTexture.FOG, false));
        otherAnimations.add(animation);
        tileAnimations.put(position, tileReplacement);
        return animation;
    }

    public Finishable createEntity(Position position, core.entities.model.Entity entity) {
        var animation = new Drop();
        animation.init(new Entity(WorldPosition.from(position, 2), entity));
        entityAnimations.put(entity.id(), animation);
        return animation;
    }

    public Finishable removeEntity(Position position, EntityID entity) {
        var animation = new Dissipate();
        animation.init(new Entity(WorldPosition.from(position), entities.findEntityByID(entity)));
        entityAnimations.put(entity, animation);
        return animation;
    }

    public Finishable showEntity(Position position, core.entities.model.Entity entity) {
        var animation = new Condense();
        animation.init(new Entity(WorldPosition.from(position), entity));
        entityAnimations.put(entity.id(), animation);
        return animation;
    }

    public Finishable hideEntity(Position position, EntityID entity) {
        var animation = new Dissipate();
        animation.init(new Entity(WorldPosition.from(position), entities.findEntityByID(entity)));
        entityAnimations.put(entity, animation);
        return animation;
    }

    public Finishable moveAlongPath(EntityID entity, List<Position> path) {
        var animation = new AnimationChain(List.of(new Drop(), new MoveAlong(path)));
        setAnimation(entity, animation);
        return animation;
    }

    @Override
    public boolean finished() {
        return false;
    }
}
