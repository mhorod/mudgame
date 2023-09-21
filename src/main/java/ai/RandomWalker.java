package ai;

import core.entities.model.Entity;
import core.model.PlayerID;
import core.model.Position;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.GameClient;
import mudgame.controls.events.NextTurn;

import java.util.List;
import java.util.Random;

@Slf4j
public class RandomWalker implements Bot {
    private final Random random = new Random();
    private final GameClient client;
    private PlayerID currentPlayer;

    public RandomWalker(GameClient client) {
        this.client = client;
        currentPlayer = client.getCore().turnView().currentPlayer();
    }

    public void update() {
        while (client.hasEvent()) {
            client.peekEvent().ifPresent(e -> {
                if (e instanceof NextTurn n)
                    currentPlayer = n.currentPlayer();
            });
            client.processEvent();
        }

        if (!client.myPlayerID().equals(currentPlayer))
            return;

        log.info("Random walker {} updating...", client.myPlayerID());
        List<Position> positions = client.getCore().fogOfWar().visiblePositions();

        if (!positions.isEmpty()) {
            if (entities().size() < 5)
                client.getControls().createEntity(randomPosition(positions));

            for (Entity e : entities())
                client.getControls().moveEntity(e.id(), randomPosition(positions));
        }

        client.getControls().completeTurn();
        currentPlayer = null;
    }

    private Position randomPosition(List<Position> positions) {
        return positions.get(random.nextInt(positions.size()));
    }

    private List<Entity> entities() {
        return client.getCore()
                .entityBoard()
                .allEntities()
                .stream().
                filter(e -> e.owner().equals(client.myPlayerID()))
                .toList();
    }
}
