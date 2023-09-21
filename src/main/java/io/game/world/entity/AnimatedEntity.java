package io.game.world.entity;

import core.model.Position;
import io.animation.Animation;
import io.animation.Finishable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnimatedEntity implements Animation {

    private final Entity entity;
    private boolean raised = false;
    private boolean falling = false;
    private EntityAnimation animation;

    public AnimatedEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public Finishable setAnimation(EntityAnimation animation) {
        this.animation = animation;
        this.animation.init(entity);
        return animation;
    }

    public void pickUp() {
        if (raised) return;
        setAnimation(new AnimationChain(List.of(new Raise(), new Hover())));
        falling = false;
        raised = true;
    }

    public void refuse() {
        if (raised || falling) return;
        setAnimation(new Shake());
    }

    public Finishable putDown() {
        if (!raised && falling)
            return animation;
        if (!raised)
            return () -> true;
        raised = false;
        falling = true;
        return setAnimation(new Drop());
    }

    public Finishable moveAlongPath(List<Optional<Position>> path) {
        ArrayList<Position> substring = new ArrayList<>();
        ArrayList<EntityAnimation> animations = new ArrayList<>();
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
        return setAnimation(animation);
    }

    public Finishable destroy() {
        return setAnimation(new Dissipate());
    }

    public Finishable show() {
        return setAnimation(new Condense());
    }

    public Finishable create() {
        falling = true;
        raised = true;
        return setAnimation(new AnimationChain(List.of(new SetZ(2), new Drop())));
    }

    @Override
    public void update(float deltaTime) {
        if (animation == null) return;
        animation.update(deltaTime);
        if (animation.finished()) {
            animation = null;
            falling = false;
        }
    }

    @Override
    public boolean finished() {
        return animation == null;
    }

}
