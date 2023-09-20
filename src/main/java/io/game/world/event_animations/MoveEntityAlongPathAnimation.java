package io.game.world.event_animations;

import io.animation.Animation;
import io.animation.Finishable;
import io.game.world.Map;
import mudgame.controls.events.MoveEntityAlongPath;

public class MoveEntityAlongPathAnimation implements Animation {
    private final Map map;
    private final MoveEntityAlongPath event;
    private final Finishable fall;
    private Finishable move;
    float totalTime = 0f;
    int currentMove = 0;

    public MoveEntityAlongPathAnimation(Map map, MoveEntityAlongPath event) {
        this.map = map;
        this.event = event;
        fall = map.entityFromID(event.entityID()).putDown();

        event.moves().forEach(move -> move.visibilityChange().positions().forEach(change ->
                map.tileFromPosition(change.position()).exist()
        ));
    }

    @Override
    public void update(float deltaTime) {
        if (!fall.finished()) return;
        if (totalTime == 0f) {
            move = map.entityFromID(event.entityID()).moveAlongPath(event.moves().stream().map(MoveEntityAlongPath.SingleMove::destination).toList());
        }
        totalTime += deltaTime;
        while (totalTime > currentMove * 0.3f && currentMove < event.moves().size()) {
            new VisibilityChangeAnimation(map, event.moves().get(currentMove).visibilityChange());
            currentMove++;
        }
        if (finished())
            event.moves().forEach(move -> move.visibilityChange().positions().forEach(change ->
                    map.tileFromPosition(change.position()).die()
            ));

    }

    @Override
    public boolean finished() {
        return currentMove >= event.moves().size() && move != null && move.finished();
    }
}
