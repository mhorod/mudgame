package io.game;

import core.entities.EntityBoardView;
import core.entities.components.Vision;
import core.entities.events.CreateEntity;
import core.entities.model.Entity;
import core.model.EntityID;
import core.model.Position;
import io.animation.AnimationController;
import io.game.world.Map;
import io.game.world.MapObserver;
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

import java.util.ArrayList;
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
    private EntityID selectedEntity;
    private Position entityPosition = new Position(2, 2);

    public GameView() {
        var clients = LocalServer.of(3);
        me = clients.get(0);
        me.processAllMessages();
        me.getCommunicator().sendMessage(new ActionMessage(new CreateEntity(
                List.of(new Vision(2)),
                me.myPlayerID(),
                new Position(2, 2)
        )));
        me.processAllMessages();
        map = new Map(me.getCore().state().terrain(), new EntityBoardView() {
            @Override
            public List<Entity> allEntities() {
                return List.of(new Entity(List.of(), new EntityID(1), me.myPlayerID()));
            }

            @Override
            public List<Entity> entitiesAt(Position position) {
                if (position.equals(entityPosition))
                    return List.of(new Entity(List.of(), new EntityID(1), me.myPlayerID()));
                else return List.of();
            }

            @Override
            public Position entityPosition(EntityID entityID) {
                return entityPosition;
            }

            @Override
            public boolean containsEntity(EntityID entityID) {
                return false;
            }

            @Override
            public Entity findEntityByID(EntityID entityID) {
                return null;
            }
        });
        animations.addAnimation(cameraController);
        animations.addAnimation(map);
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
                map.objectAt(click.position(), bank, camera, new MapObserver() {
                    @Override
                    public void onEntity(EntityID id) {
                        if (selectedEntity == null) {
                            map.pickUp(id);
                            selectedEntity = id;
                        } else if (selectedEntity.equals(id)) {
                            map.putDown(id);
                            map.setPath(List.of());
                            selectedEntity = null;
                        } else {
                            map.putDown(selectedEntity);
                            map.pickUp(id);
                            map.setPath(List.of());
                            selectedEntity = id;
                        }
                    }

                    @Override
                    public void onTile(Position position) {
                        if (selectedEntity != null) {
                            map.putDown(selectedEntity);
                            map.setPath(List.of());
                            map.moveAlongPath(selectedEntity, pathBetween(entityPosition, position));
                            entityPosition = position;
                            selectedEntity = null;
                        }
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

            }

            @Override
            public void onTile(Position position) {
                if (selectedEntity != null)
                    map.setPath(pathBetween(entityPosition, position));
            }
        });
        dragDetector.update(input.mouse(), input.deltaTime());
        animations.update(input.deltaTime());
    }

    private static List<Position> pathBetween(Position a, Position b) {
        ArrayList<Position> result = new ArrayList<>();
        while (!a.equals(b)) {
            result.add(a);
            var dx = b.x() - a.x();
            var dy = b.y() - a.y();
            if (dx > 0)
                a = new Position(a.x() + 1, a.y());
            else if (dx < 0)
                a = new Position(a.x() - 1, a.y());
            else if (dy > 0)
                a = new Position(a.x(), a.y() + 1);
            else if (dy < 0)
                a = new Position(a.x(), a.y() - 1);
        }
        result.add(b);
        return result;
    }

}
