package io.game;

import ai.Bot;
import ai.RandomWalker;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import io.animation.Animation;
import io.animation.AnimationController;
import io.animation.Finishable;
import io.game.ui.HUD;
import io.game.world.Map;
import io.game.world.MapObserver;
import io.game.world.controller.WorldController;
import io.model.engine.Canvas;
import io.model.engine.TextManager;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.views.SimpleView;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.GameClient;
import middleware.clients.ServerClient;
import middleware.communication.SocketDevice.SocketConnectionBuilder;
import middleware.local.LocalServer;
import middleware.remote.RemoteNetworkClient;
import mudgame.controls.events.*;
import mudgame.server.state.ClassicServerStateSupplier;
import mudgame.server.state.ServerState;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GameView extends SimpleView {
    private final AnimationController<Animation> animations = new AnimationController<>();
    private final Map map;
    private MapView mapView;
    private final Camera camera = new Camera();
    private final HUD hud;
    private final CameraController cameraController = new CameraController(camera);
    private final DragDetector dragDetector = new DragDetector() {
        @Override
        protected void onDrag(float dx, float dy, float deltaTime) {
            cameraController.drag(dx, dy, deltaTime);
        }
    };
    private final WorldController worldController;

    private final GameClient me;
    private Finishable eventAnimation;
    private final List<Bot> bots = new ArrayList<>();

    public GameView() {
        ServerState serverState = new ClassicServerStateSupplier().get(4);
        var server = new LocalServer(serverState);

        if (true) {
            me = server.getClients().get(0);
            for (int i = 1; i < server.playerCount(); i++)
                bots.add(new RandomWalker(server.getClient(i)));
        } else {
            // --------------------------------------------------
            // if this causes many merge conflicts remove it
            try {
                RemoteNetworkClient.GLOBAL_CLIENT.connect(
                        new SocketConnectionBuilder("localhost", 6789));

                while (RemoteNetworkClient.GLOBAL_CLIENT.getServerClient().isEmpty())
                    RemoteNetworkClient.GLOBAL_CLIENT.processAllMessages();

                ServerClient serverClient = RemoteNetworkClient.GLOBAL_CLIENT.getServerClient()
                        .orElseThrow();
                serverClient.createRoom(new PlayerID(0), 5);
                serverClient.startGame();

                while (serverClient.getGameClient().isEmpty())
                    RemoteNetworkClient.GLOBAL_CLIENT.processAllMessages();

                me = serverClient.getGameClient().orElseThrow();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
            // --------------------------------------------------
        }

        map = new Map(me.getCore().terrain(), me.getCore().entityBoard());
        hud = new HUD(me.getCore().turnView());
        animations.addAnimation(cameraController);
        animations.addAnimation(map);
        worldController = new WorldController(
                map,
                hud,
                me.getCore().myPlayerID(),
                me.getCore().entityBoard(),
                me.getCore().terrain(),
                me.getCore().pathfinder(),
                me.getCore().spawnManager(),
                me.getControls()
        );
    }

    private void processEvent(Event event) {
        log.debug("Processing event: {}", event);
        if (event instanceof MoveEntityAlongPath e) {
            eventAnimation = map.animate(e);
            worldController.onMoveEntityAlongPath(e);
        } else if (event instanceof VisibilityChange e) {
            eventAnimation = map.animate(e);
            worldController.onVisibilityChange(e);
        } else if (event instanceof SpawnEntity e) {
            eventAnimation = map.animate(e);
            worldController.onSpawnEntity(e);
        } else if (event instanceof RemoveEntity e) {
            eventAnimation = map.animate(e);
            worldController.onRemoveEntity(e);
        } else if (event instanceof NextTurn e) {
            worldController.onNextTurn(e);
        }
        me.processEvent();
    }

    private void processEvents() {
        if (eventAnimation != null && eventAnimation.finished())
            eventAnimation = null;
        if (eventAnimation == null)
            me.peekEvent().ifPresent(this::processEvent);
    }


    @Override
    public void draw(Canvas canvas) {
        if (mapView != null)
            mapView.draw(canvas);
        hud.draw(canvas);
    }

    @Override
    public void update(Input input, TextureBank bank, TextManager mgr) {
        for (Bot bot : bots)
            bot.update();
        RemoteNetworkClient.GLOBAL_CLIENT.processAllMessages();
        processEvents();
        worldController.update();

        if (mapView != null) {
            input.events().forEach(event -> event.accept(new EventHandler() {
                @Override
                public void onClick(Click click) {
                    if (!hud.click(click.position(), worldController))
                        mapView.objectAt(click.position(), new MapObserver() {
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
            mapView.objectAt(input.mouse().position(), new MapObserver() {
                @Override
                public void onEntity(EntityID id) {
                    worldController.onEntityHover(id);
                }

                @Override
                public void onTile(Position position) {
                    worldController.onTileHover(position);
                }
            });
        }
        hud.update(input.mouse().position(), input.mouse().leftPressed(), mgr);
        dragDetector.update(input.mouse(), input.deltaTime());
        animations.update(input.deltaTime());
        camera.setAspectRatio(input.window().height() / input.window().width());
        mapView = map.getView(bank, camera);
    }


}
