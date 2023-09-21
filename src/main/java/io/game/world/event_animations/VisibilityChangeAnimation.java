package io.game.world.event_animations;

import io.animation.Animation;
import io.animation.Finishable;
import io.game.world.Map;
import io.model.engine.Color;
import mudgame.controls.events.VisibilityChange;

import java.util.ArrayList;
import java.util.Optional;

public class VisibilityChangeAnimation implements Animation {
    private final Map map;
    private final VisibilityChange event;
    private final Finishable all;

    public VisibilityChangeAnimation(Map map, VisibilityChange event) {
        this.map = map;
        this.event = event;
        ArrayList<Finishable> animations = new ArrayList<>();
        event.positions().forEach(pvc -> {
            if (pvc instanceof VisibilityChange.ShowPosition show) {
                var tile = map.tileFromPosition(show.position());
                tile.setTerrain(show.terrain());
                tile.setEntities(show.entities());
                tile.setColor(Optional.ofNullable(show.positionOwner()).map(Color::fromPlayerId).orElse(Color.WHITE));
                show.entities().forEach(entity -> map.createEntity(show.position(), entity).show());
                animations.add(tile.uncover(map::fog));
            } else if (pvc instanceof VisibilityChange.HidePosition hide) {
                animations.add(map.tileFromPosition(hide.position()).cover(map::fog));
                map.tileFromPosition(hide.position()).getEntities(map.getAnimatedEntities())
                        .forEach(entity -> map.createEntity(hide.position(), entity).destroy());
            }
        });
        all = Finishable.all(animations);
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public boolean finished() {
        return all.finished();
    }
}
