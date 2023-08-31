package io.game;

import io.animation.Animation;
import io.animation.Easer;
import io.animation.Floater;
import io.model.ScreenPosition;

public class CameraController implements Animation {
    private final Camera camera;

    private final Floater cameraMoveAnimation = new Floater() {
        @Override
        protected void onUpdate(float x, float y) {
            camera.offsetX = x;
            camera.offsetY = y;
        }
    };

    private ScreenPosition pivot = new ScreenPosition(0, 0);
    private final Easer zoomAnimation;
    private final Floater moveAnimation;

    public CameraController(Camera camera) {
        this.camera = camera;
        moveAnimation = new Floater() {
            @Override
            protected void onUpdate(float x, float y) {
                camera.offsetX = x;
                camera.offsetY = y;
            }
        };
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
        zoomAnimation.setTarget((float) (zoomAnimation.getTarget() * Math.pow(0.85, amount)));
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
