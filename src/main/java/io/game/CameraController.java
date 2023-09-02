package io.game;

import io.animation.Animation;
import io.animation.Easer;
import io.animation.Floater;
import io.model.ScreenPosition;

public class CameraController implements Animation {

    private static final float SCROLL_FORCE = 0.85f;
    private static final float MIN_ZOOM = 0.02f;
    private static final float MAX_ZOOM = 1f;

    private ScreenPosition pivot = new ScreenPosition(0, 0);
    private final Easer zoomAnimation;
    private final Floater moveAnimation;

    public CameraController(Camera camera) {
        moveAnimation = new Floater() {
            @Override
            protected void onUpdate(float x, float y) {
                camera.offsetX = x;
                camera.offsetY = y;
            }
        };
        moveAnimation.setValue(0.5f, 1);
        zoomAnimation = new Easer(camera.getTileWidth()) {
            @Override
            public void onUpdate(float value) {
                camera.setZoom(pivot, value);
                moveAnimation.setValue(camera.offsetX, camera.offsetY);
            }
        };
    }

    public void drag(float dx, float dy, float deltaTime) {
        moveAnimation.add(dx, dy, deltaTime);
    }

    public void scroll(ScreenPosition pivot, float amount) {
        this.pivot = pivot;
        float rawZoom = (float) (zoomAnimation.getTarget() * Math.pow(SCROLL_FORCE, amount));
        float zoom = Math.min(MAX_ZOOM, Math.max(rawZoom, MIN_ZOOM));
        zoomAnimation.setTarget(zoom);
    }

    @Override
    public void update(float deltaTime) {
        zoomAnimation.update(deltaTime);
        moveAnimation.update(deltaTime);
    }


    @Override
    public boolean finished() {
        return false;
    }
}
