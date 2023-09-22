package io.game.world;

import io.game.Camera;
import io.game.world.entity.Entity;
import io.game.world.tile.Tile;
import io.model.Drawable;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;

import java.util.ArrayList;
import java.util.List;

public class MapView implements Drawable {
    private final ArrayList<WorldEntity> entities;
    private final Camera camera;
    private final TextureBank textureBank;

    public MapView(List<WorldEntity> entities, Camera camera, TextureBank textureBank) {
        this.entities = new ArrayList<>(entities);
        this.entities.sort((a, b) -> {
            var zDiff = a.getLayer() - b.getLayer();
            if (zDiff != 0) return zDiff;
            var valA = a.getPosition().x() + a.getPosition().y();
            var valB = b.getPosition().x() + b.getPosition().y();
            return (int) (100 * (valA - valB));
        });
        this.camera = camera;
        this.textureBank = textureBank;
    }

    @Override
    public void draw(Canvas canvas) {
        entities.forEach(entity -> entity.draw(canvas, camera));
    }

    public void objectAt(ScreenPosition position, MapObserver listener) {
        for (int i = entities.size() - 1; i >= 0; i--) {
            var entity = entities.get(i);
            if (entity.contains(position, textureBank, camera)) {
                if (entity instanceof Tile) {
                    listener.onTile(((Tile) entity).getGamePosition());
                    return;
                } else if (entity instanceof Entity) {
                    listener.onEntity(((Entity) entity).getId());
                    return;
                }
            }
        }
    }
}
