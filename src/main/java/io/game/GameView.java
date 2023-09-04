package io.game;

import core.entities.events.CreateEntity;
import core.entities.events.HideEntity;
import core.entities.events.MoveEntity;
import core.entities.events.RemoveEntity;
import core.entities.events.ShowEntity;
import core.entities.events.SpawnEntity;
import mudgame.events.Event;
import mudgame.events.Event.Action;
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
import lombok.extern.slf4j.Slf4j;
import middleware.Client;
import middleware.LocalServer;
import middleware.messages_to_server.ActionMessage;

import static core.entities.model.EntityType.PAWN;

@Slf4j
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
                    public void createEntity(Position position) {
                        Action action = new CreateEntity(PAWN, me.myPlayerID(), position);
                        me.getCommunicator().sendMessage(new ActionMessage(action));
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
        log.debug("Processing event: {}", event);
        if (event instanceof MoveEntity e) {
            eventObserved = true;
            worldController.onMoveEntity(e);
        } else if (event instanceof SetTerrain e) {
            eventObserved = true;
            worldController.onSetTerrain(e);
        } else if (event instanceof SpawnEntity e) {
            eventObserved = true;
            worldController.onPlaceEntity(e);
        } else if (event instanceof RemoveEntity e) {
            eventObserved = true;
            worldController.onRemoveEntity(e);
        } else if (event instanceof ShowEntity e) {
            eventObserved = true;
            worldController.onShowEntity(e);
        } else if (event instanceof HideEntity e) {
            eventObserved = true;
            worldController.onHideEntity(e);
        }
    }

    private boolean canEatEvent() {
        return me.peekEvent().stream().anyMatch(
                event -> !(event instanceof MoveEntity)
                         && !(event instanceof SetTerrain)
                         && !(event instanceof SpawnEntity)
                         && !(event instanceof RemoveEntity)
                         && !(event instanceof ShowEntity)
                         && !(event instanceof HideEntity)
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
