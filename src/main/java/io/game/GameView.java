package io.game;

import core.entities.events.MoveEntity;
import core.entities.events.PlaceEntity;
import core.entities.events.RemoveEntity;
import core.events.Event;
import core.model.EntityID;
import core.model.Position;
import core.terrain.events.SetTerrain;
import io.animation.AnimationController;
import io.game.world.Map;
import io.game.world.MapObserver;
import io.game.world.controller.Controls;
import io.game.world.controller.WorldController;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.views.SimpleView;
import middleware.Client;
import middleware.LocalServer;
import middleware.messages_to_server.ActionMessage;

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
    private final WorldController worldController;

    private final Client me;
    private boolean eventObserved = false;

    public GameView() {
        var clients = LocalServer.of(5);
        me = clients.get(0);
        me.processAllMessages();
        map = new Map(me.getCore().state().terrain(), me.getCore().state().entityBoard());
        animations.addAnimation(cameraController);
        animations.addAnimation(map);
        worldController = new WorldController(
                map,
                me.getCore().state().entityBoard(),
                me.getCore().state().terrain(),
                new Controls() {
                    @Override
                    public void moveEntity(EntityID id, Position destination) {
                        me.getCommunicator()
                                .sendMessage(new ActionMessage(new MoveEntity(id, destination)));
                    }

                    @Override
                    public void nextEvent() {
                        do {
                            me.processEvent();
                        } while (canEatEvent());
                        eventObserved = false;
                        var maybeEvent = me.peekEvent();
                        maybeEvent.ifPresent(GameView.this::processEvent);
                    }
                });
    }

    private void processEvent(Event event) {
        if (event instanceof MoveEntity) {
            eventObserved = true;
            worldController.onMoveEntity((MoveEntity) event);
        } else if (event instanceof SetTerrain) {
            eventObserved = true;
            worldController.onSetTerrain((SetTerrain) event);
        } else if (event instanceof PlaceEntity) {
            eventObserved = true;
            worldController.onPlaceEntity((PlaceEntity) event);
        } else if (event instanceof RemoveEntity) {
            eventObserved = true;
            worldController.onRemoveEntity((RemoveEntity) event);
        }
    }

    private boolean canEatEvent() {
        return me.peekEvent().stream().anyMatch(
                event -> !(event instanceof MoveEntity)
                        && !(event instanceof SetTerrain)
                        && !(event instanceof PlaceEntity)
                        && !(event instanceof RemoveEntity)
        );
    }

    @Override
    public void draw(Canvas canvas) {
        map.draw(canvas, camera);
    }

    @Override
    public void update(Input input, TextureBank bank) {
        me.processAllMessages();
        worldController.update();
        if (!eventObserved) {
            while (canEatEvent())
                me.processEvent();
            me.peekEvent().ifPresent(this::processEvent);
        }

        input.events().forEach(event -> event.accept(new EventHandler() {
            @Override
            public void onClick(Click click) {
                map.objectAt(click.position(), bank, camera, new MapObserver() {
                    @Override
                    public void onEntity(EntityID id) {
                        worldController.onEntityClick(id);
                    }

                    @Override
                    public void onTile(Position position) {
                        worldController.onTileClick(position);
                    }
                });
            }

            @Override
            public void onScroll(Scroll scroll) {
                cameraController.scroll(input.mouse().position(), scroll.amount());
            }
        }));
        map.objectAt(input.mouse().position(), bank, camera, new MapObserver() {
            @Override
            public void onEntity(EntityID id) {
                worldController.onEntityHover(id);
            }

            @Override
            public void onTile(Position position) {
                worldController.onTileHover(position);
            }
        });
        dragDetector.update(input.mouse(), input.deltaTime());
        animations.update(input.deltaTime());
    }


}
