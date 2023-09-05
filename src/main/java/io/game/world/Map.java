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
import io.game.world.entity.*;
import io.game.world.tile.PlannedChange;
import io.game.world.tile.Tile;
import io.game.world.tile.TileAnimation;
import io.game.world.tile.TileKind;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;
import mudgame.controls.events.VisibilityChange;

import java.util.*;

public class Map implements Animation {
    HashMap<EntityID, EntityAnimation> entityAnimations = new HashMap<>();
    ArrayList<EntityAnimation> otherAnimations = new ArrayList<>();
    private final TerrainView terrain;
    private final EntityBoardView entities;
    private final ArrayList<Position> path = new ArrayList<>();
    private Collection<Position> highlightedTiles = null;
    HashMap<Position, TileAnimation> tmpTiles = new HashMap<>();

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

    private Finishable setAnimation(EntityID entityID, EntityAnimation animation) {
        animation.init(entityFromID(entityID));
        entityAnimations.put(entityID, animation);
        return animation;
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

    private Tile fog(Position pos) {
        var left = terrain.terrainAt(new Position(pos.x(), pos.y() + 1)) == TerrainType.VOID;
        var right = terrain.terrainAt(new Position(pos.x() + 1, pos.y())) == TerrainType.VOID;
        if (left && right)
            return new Tile(pos, WorldTexture.FOG_TALL);
        if (left)
            return new Tile(pos, WorldTexture.FOG_LEFT);
        if (right)
            return new Tile(pos, WorldTexture.FOG_RIGHT);
        return new Tile(pos, WorldTexture.FOG);
    }

    public void draw(Canvas canvas, Camera camera) {
        ArrayList<Tile> fogTiles = new ArrayList<>();
        ArrayList<WorldEntity> highlightTiles = new ArrayList<>();
        ArrayList<WorldEntity> entitiesToDraw = new ArrayList<>();
        camera.forAllVisibleTiles(canvas.getAspectRatio(), pos -> {
            if (tmpTiles.containsKey(pos)) {
                var tile = tmpTiles.get(pos);
                switch (tile.getKind()) {
                    case TILE_DARK -> new Tile(pos, WorldTexture.TILE_DARK).draw(canvas, camera);
                    case TILE_LIGHT -> new Tile(pos, WorldTexture.TILE_LIGHT).draw(canvas, camera);
                    case FOG -> fogTiles.add(fog(pos));
                }
                entitiesToDraw.addAll(tile.getEntities().stream().filter(
                        entity -> !entityAnimations.containsKey(entity.getId())
                ).toList());
                entitiesToDraw.addAll(tile.otherWorldEntities());
            } else {
                var tile = terrain.terrainAt(pos);
                switch (tile) {
                    case UNKNOWN -> fogTiles.add(fog(pos));
                    case WATER -> new Tile(pos, WorldTexture.TILE_LIGHT).draw(canvas, camera);
                    case LAND -> new Tile(pos, WorldTexture.TILE_DARK).draw(canvas, camera);
                }
                if (highlightedTiles != null && !highlightedTiles.contains(pos) &&
                        tile == TerrainType.LAND)
                    highlightTiles.add(
                            new WorldEntity(WorldPosition.from(pos), WorldTexture.TILE_HIGHLIGHT,
                                    false)
                    );
                entitiesToDraw.addAll(
                        entities.entitiesAt(pos).stream()
                                .filter(e -> !entityAnimations.containsKey(e.id()))
                                .map(e -> entityFromID(e.id()))
                                .toList()
                );
            }


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
        setAnimation(entity, new AnimationChain(List.of(new Raise(), new Hover())));
    }

    public Finishable putDown(EntityID entity) {
        return setAnimation(entity, new Drop());
    }

    public Finishable getAnimation(EntityID entity) {
        return entityAnimations.get(entity);
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

    public Finishable moveAlongPath(EntityID entity, List<Optional<Position>> path) {
        ArrayList<Position> substring = new ArrayList<>();
        ArrayList<EntityAnimation> animations = new ArrayList<>(List.of(new Drop()));
        path.forEach(pos -> {
            if (!substring.isEmpty() && pos.isEmpty()) {
                animations.add(new MoveAlong(new ArrayList<>(substring)));
                substring.clear();
            }
            pos.ifPresent(substring::add);
        });
        if (!substring.isEmpty())
            animations.add(new MoveAlong(new ArrayList<>(substring)));
        var animation = new AnimationChain(animations);
        setAnimation(entity, animation);
        return animation;
    }

    public void showIn(float time, VisibilityChange.ShowPosition event) {
        if (!tmpTiles.containsKey(event.position()))
            tmpTiles.put(event.position(), new TileAnimation(
                    event.position(),
                    TileKind.from(terrain.terrainAt(event.position())),
                    entities.entitiesAt(event.position())
            ));
        tmpTiles.get(event.position()).changeIn(time, new PlannedChange(TileKind.from(event.terrain()), event.entities()));
    }

    public void hideIn(float time, Position position) {
        if (!tmpTiles.containsKey(position))
            tmpTiles.put(position, new TileAnimation(
                    position,
                    TileKind.from(terrain.terrainAt(position)),
                    entities.entitiesAt(position)
            ));
        tmpTiles.get(position).changeIn(time, new PlannedChange(TileKind.FOG, List.of()));
    }

    @Override
    public boolean finished() {
        return false;
    }
}
