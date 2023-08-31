package io.game;

import io.animation.AnimationController;
import io.game.world.Map;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.views.SimpleView;

public class GameView extends SimpleView {
    private final AnimationController animations = new AnimationController();
    private final Map map = new Map(10, 10);
    private final Camera camera = new Camera();
    private final CameraController cameraController = new CameraController(camera);
    private final DragDetector dragDetector = new DragDetector() {
        @Override
        protected void onDrag(float dx, float dy, float deltaTime) {
            cameraController.drag(dx, dy, deltaTime);
        }
    };

    public GameView() {
        animations.addAnimation(cameraController);
    }

    @Override
    public void draw(Canvas canvas) {
        map.draw(canvas, camera);
    }

    @Override
    public void update(Input input, TextureBank bank) {
        input.events().forEach(event -> event.accept(new EventHandler() {
            @Override
            public void onClick(Click click) {
                System.out.println(click);
                map.getEntityAt(click.position(), bank, camera);
            }

            @Override
            public void onScroll(Scroll scroll) {
                cameraController.scroll(input.mouse().position(), scroll.amount());
            }
        }));
        dragDetector.update(input.mouse(), input.deltaTime());
        animations.update(input.deltaTime());
    }
}
