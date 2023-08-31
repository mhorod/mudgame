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
import middleware.SimpleLocalServer;
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

    private final Client me;

    public GameView() {
        animations.addAnimation(cameraController);
        var server = new SimpleLocalServer(3);
        me = server.clients.get(0);
        me.processAllMessages();
        me.getCommunicator().sendMessage(new ActionMessage(new CreateEntity(
                List.of(new Vision(2)),
                me.myPlayerID(),
                new Position(2, 2)
        )));
        me.processAllMessages();
        map = new Map(me.getCore().state().terrain());
    }

    @Override
    public void draw(Canvas canvas) {
        map.draw(canvas, camera);
    }

    @Override
    public void update(Input input, TextureBank bank) {
        me.processAllMessages();
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
