package mudgame.integration.replays;

import core.entities.model.Entity;
import core.model.PlayerID;
import core.model.Position;
import core.resources.PlayerResourceManager;
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
public class Replays {
    void checkIntegrityOfClient(MudServerCore serverCore, MudClientCoreView clientCore) {
        PlayerID myPlayer = clientCore.myPlayerID();

        for (Position pos : clientCore.fogOfWar().visiblePositions()) {
            Optional<PlayerID> ownerByServer = serverCore.state().claimedArea().owner(pos);
            Optional<PlayerID> ownerByClient = clientCore.claimedArea().owner(pos);

            PlayerResourcesView serverResources = serverCore.state().resourceManager().playerResources(myPlayer);
            PlayerResourcesView clientResources = clientCore.playerResources();

            List<Entity> serverEntities = serverCore.state().entityBoard().entitiesAt(pos);
            List<Entity> clientEntities = clientCore.entityBoard().entitiesAt(pos);

            assertThat(ownerByClient).isEqualTo(ownerByServer);
            assertThat(clientResources).isEqualTo(serverResources);
            assertThat(clientEntities).isEqualTo(serverEntities);
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
}
