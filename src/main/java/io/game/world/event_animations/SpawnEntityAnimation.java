package io.game.world.event_animations;

import io.animation.Animation;
import io.animation.Finishable;
import io.game.world.Map;
import mudgame.controls.events.SpawnEntity;

public class SpawnEntityAnimation implements Animation {
    private final Animation visibilityChange;
    private final Finishable fall;

    public SpawnEntityAnimation(Map map, SpawnEntity event) {
        visibilityChange = new VisibilityChangeAnimation(map, event.visibilityChange());
        fall = map.createEntity(event.position(), event.entity()).create();
    }

    @Override
    public void update(float deltaTime) {
        visibilityChange.update(deltaTime);
    }

    @Override
    public boolean finished() {
        return visibilityChange.finished() && fall.finished();
    }
}
