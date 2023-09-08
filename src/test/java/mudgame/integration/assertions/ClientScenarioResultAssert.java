package mudgame.integration.assertions;

import core.entities.model.Entity;
import core.event.Event;
import core.model.PlayerID;
import core.model.Position;
import lombok.RequiredArgsConstructor;
import mudgame.client.ClientGameState;
import mudgame.integration.utils.ScenarioResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public final class ClientScenarioResultAssert {

    private final ClientGameState state;
    private final List<Event> receivedEvents;


    public static ClientScenarioResultAssert assertThatClient(
            ScenarioResult scenarioResult, PlayerID player
    ) {
        return new ClientScenarioResultAssert(
                scenarioResult.clientState(player),
                scenarioResult.clientEvents(player)
        );
    }

    public ClientScenarioResultAssert seesEntities(Entity... entities) {
        assertThat(entities).allMatch(e -> e.equals(state.entityBoard().findEntityByID(e.id())));
        return this;
    }

    public ClientScenarioResultAssert doesNotSeeEntities(Entity... entities) {
        assertThat(entities).noneMatch(e -> e.equals(state.entityBoard().findEntityByID(e.id())));
        return this;
    }

    public ClientScenarioResultAssert sees(Position... positions) {
        assertThat(positions).allMatch(p -> state.fogOfWar().isVisible(p));
        return this;
    }

    public ClientScenarioResultAssert doesNotSee(Position... positions) {
        assertThat(positions).noneMatch(p -> state.fogOfWar().isVisible(p));
        return this;
    }

    @SafeVarargs
    public final ClientScenarioResultAssert receivedEventTypes(
            Class<? extends Event>... eventTypes
    ) {
        assertThat(receivedEvents).hasSize(eventTypes.length);
        for (int i = 0; i < receivedEvents.size(); i++)
            assertThat(receivedEvents.get(i)).isInstanceOf(eventTypes[i]);
        return this;
    }

    public ClientScenarioResultAssert turn(PlayerID player) {
        assertThat(state.playerManager().getCurrentPlayer()).isEqualTo(player);
        return this;
    }
}

