package io.game;

import core.entities.components.Vision;
import core.entities.events.CreateEntity;
import core.model.Position;
import io.animation.AnimationController;
import io.game.world.Map;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.views.SimpleView;
import middleware.Client;
import middleware.GameClient;
import middleware.local.LocalServer;
import middleware.messages_to_server.ActionMessage;

import java.util.List;

public class GameView extends SimpleView {
    private final AnimationController animations = new AnimationController();
    private final Map map;
    private final Camera camera = new Camera();
    private final CameraController cameraController = new CameraController(camera);
    private final DragDetector dragDetector = new DragDetector() {
        @Override
        protected void onDrag(float dx, float dy, float deltaTime) {
            cameraController.drag(dx, dy, deltaTime);
        }
    };

    private final GameClient me;

    public GameView() {
        animations.addAnimation(cameraController);
        var clients = new LocalServer(3).clients();
        me = clients.get(0);
        me.sendAction(new CreateEntity(
                List.of(new Vision(2)),
                me.myPlayerID(),
                new Position(2, 2)
        ));
        map = new Map(me.getGameState().terrain());
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
