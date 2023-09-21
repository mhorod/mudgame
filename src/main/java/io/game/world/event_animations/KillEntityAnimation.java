package io.game.world.event_animations;

import io.animation.Animation;
import io.animation.Finishable;
import io.game.world.Map;
import io.game.world.entity.Dissipate;
import mudgame.controls.events.KillEntity;

public class KillEntityAnimation implements Animation {
    private final Finishable all;

    public KillEntityAnimation(Map map, KillEntity event) {
        all = Finishable.all(
                map.entityFromID(event.entityID()).setAnimation(new Dissipate()),
                map.animate(event.visibilityChange())
        );
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public boolean finished() {
        return all.finished();
    }
}
