package io.game.world.event_animations;

import core.model.Position;
import io.animation.Animation;
import io.animation.Finishable;
import io.game.world.Map;
import io.game.world.entity.JumpTo;
import io.game.world.entity.Shake;
import mudgame.controls.events.AttackEntityEvent;

public class AttackEntityAnimation implements Animation {

    private final AttackEntityEvent event;
    private final Map map;

    private final Position from, to;

    private Finishable attack;
    private Finishable retreat;

    public AttackEntityAnimation(Map map, AttackEntityEvent event, Position from, Position to) {
        this.event = event;
        this.map = map;
        this.from = from;
        this.to = to;
        attack = map.entityFromID(event.attacker()).setAnimation(new JumpTo(to));
    }

    @Override
    public void update(float deltaTime) {
        if (!attack.finished()) return;
        if (retreat == null)
            retreat = Finishable.all(
                    map.entityFromID(event.attacker()).setAnimation(new JumpTo(from)),
                    map.entityFromID(event.attacked()).setAnimation(new Shake())
            );
    }

    @Override
    public boolean finished() {
        return retreat != null && retreat.finished();
    }
}
