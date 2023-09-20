package io.game.world.event_animations;

import io.animation.Animation;
import io.animation.Finishable;
import io.game.world.Map;
import mudgame.controls.events.RemoveEntity;

public class RemoveEntityAnimation implements Animation {

    private final Finishable death;

    public RemoveEntityAnimation(Map map, RemoveEntity event) {
        death = map.entityFromID(event.entityID()).destroy();
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public boolean finished() {
        return death.finished();
    }
}
