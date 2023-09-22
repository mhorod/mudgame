package mudgame.integration;

import core.entities.model.Entity;
import core.entities.model.EntityType;
import core.entities.model.components.Health;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import core.resources.PlayerResourcesView;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.GameClient;
import middleware.local.LocalServer;
import mudgame.client.MudClientCoreView;
import mudgame.controls.actions.Action;
import mudgame.server.MudServerCore;
import mudgame.server.actions.ActionRecorder;
import mudgame.server.actions.ActionRecorder.ActionWithActor;
import mudgame.server.actions.ActionRecorder.ActionsAndState;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ReplayTest {
    private record EntityData(EntityID entityID, EntityType type, PlayerID owner, Optional<Health> health) {
        public EntityData(Entity entity) {
            this(entity.id(), entity.type(), entity.owner(), entity.getHealth());
        }
    }

    void checkIntegrityOfClient(MudServerCore serverCore, MudClientCoreView clientCore) {
        PlayerID myPlayerID = clientCore.myPlayerID();
        log.info("player: {}", myPlayerID);

        List<Position> serverVisible = serverCore.state().fogOfWar().playerFogOfWarView(myPlayerID).visiblePositions();
        List<Position> clientVisible = clientCore.fogOfWar().visiblePositions();

        PlayerResourcesView serverResources = serverCore.state().resourceManager().playerResources(myPlayerID);
        PlayerResourcesView clientResources = clientCore.playerResources();

        assertThat(clientVisible).containsExactlyInAnyOrderElementsOf(serverVisible);
        assertThat(clientResources).isEqualTo(serverResources);

        for (Position pos : clientVisible) {
            if (!serverCore.state().terrain().contains(pos))
                continue;

            Optional<PlayerID> serverOwner = serverCore.state().claimedArea().owner(pos);
            Optional<PlayerID> clientOwner = clientCore.claimedArea().owner(pos);

            List<EntityData> serverEntities = serverCore.state().entityBoard().entitiesAt(pos).stream().map(EntityData::new).toList();
            List<EntityData> clientEntities = clientCore.entityBoard().entitiesAt(pos).stream().map(EntityData::new).toList();

            assertThat(clientOwner).isEqualTo(serverOwner);
            assertThat(clientEntities).containsExactlyInAnyOrderElementsOf(serverEntities);
        }
    }

    void checkIntegrity(LocalServer server) {
        for (GameClient client : server.getClients()) {
            while (client.hasEvent())
                client.processEvent();

            checkIntegrityOfClient(server.core(), client.getCore());
        }
    }

    void runScenarioFromBase64(String base64) {
        ActionsAndState actionsAndState = ActionRecorder.fromBase64(base64);
        LocalServer server = new LocalServer(actionsAndState.state());

        for (ActionWithActor actionWithActor : actionsAndState.actions()) {
            Action action = actionWithActor.action();
            PlayerID actor = actionWithActor.actor();

            log.info("{} from {}", action, actor);

            server.processAction(action, actor);
            checkIntegrity(server);
        }
    }

    @SneakyThrows
    void runScenario() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String scenarioName = stack[2].getMethodName();
        InputStream stream = getClass().getResourceAsStream(scenarioName + ".log");
        assertThat(stream).isNotNull();
        try (Scanner scanner = new Scanner(stream)) {
            runScenarioFromBase64(scanner.next());
        }
    }

    @Test
    void scenario1() {
        runScenario();
    }

    @Test
    void scenario2() {
        runScenario();
    }

    @Test
    void scenario3() {
        runScenario();
    }

    @Test
    void scenario4() {
        runScenario();
    }
}
