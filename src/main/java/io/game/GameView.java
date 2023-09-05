package io.game;

import core.event.Action;
import core.event.Event;
import core.model.EntityID;
import core.model.Position;
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
import middleware.clients.GameClient;
import middleware.local.LocalServer;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.actions.MoveEntity;
import mudgame.controls.events.HideEntity;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.ShowEntity;
import mudgame.controls.events.SpawnEntity;
import mudgame.controls.events.VisibilityChange;

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

    private final GameClient me;
    private boolean eventObserved = false;

    public GameView() {
        var server = new LocalServer(5);
        me = server.getClients().get(0);
        map = new Map(me.getCore().state().terrain(), me.getCore().state().entityBoard());
        animations.addAnimation(cameraController);
        animations.addAnimation(map);
        worldController = new WorldController(
                map,
                me.getCore().state().entityBoard(),
                me.getCore().state().terrain(),
                me.getCore().pathfinder(),
                new Controls() {
                    @Override
                    public void moveEntity(EntityID id, Position destination) {
                        me.sendAction(new MoveEntity(id, destination));
                    }

                    @Override
                    public void createEntity(Position position) {
                        Action action = new CreateEntity(PAWN, me.myPlayerID(), position);
                        me.sendAction(action);
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
        if (event instanceof MoveEntityAlongPath e) {
            eventObserved = true;
            worldController.onMoveEntityAlongPath(e);
        } else if (event instanceof VisibilityChange e) {
            eventObserved = true;
            worldController.onVisibilityChange(e);
        } else if (event instanceof SpawnEntity e) {
            eventObserved = true;
            worldController.onSpawnEntity(e);
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
                event -> !(event instanceof MoveEntityAlongPath)
                         && !(event instanceof VisibilityChange)
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
