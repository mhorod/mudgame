package ai;

import core.entities.model.Entity;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.GameClient;

import java.util.List;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
public class RandomWalker implements Bot {
    private final Random random = new Random();
    private final GameClient client;

    public void update() {
        while (client.hasEvent())
            client.processEvent();

        if (!client.getCore()
                .state()
                .playerManager()
                .getCurrentPlayer()
                .equals(client.myPlayerID()))
            return;

        log.info("Random walker {} updating...", client.myPlayerID());
        List<Position> positions = client.getCore().state().fogOfWar().visiblePositions();
        if (entities().size() < 5)
            client.getControls().createEntity(randomPosition(positions));

        for (Entity e : entities())
            client.getControls().moveEntity(e.id(), randomPosition(positions));
        client.getControls().completeTurn();
    }

    private Position randomPosition(List<Position> positions) {
        return positions.get(random.nextInt(positions.size()));
    }

    private List<Entity> entities() {
        return client.getCore()
                .state().
                entityBoard()
                .allEntities()
                .stream().
                filter(e -> e.owner().equals(client.myPlayerID()))
                .toList();
    }
}
