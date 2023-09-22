package testutils.integration.assertions;

import core.entities.model.Entity;
import core.entities.model.EntityType;
import testutils.integration.utils.ScenarioResult;
import mudgame.controls.events.Event;
import core.model.PlayerID;
import core.model.Position;
import core.resources.ResourceType;
import core.spawning.PlayerSpawnManager;
import lombok.RequiredArgsConstructor;
import mudgame.client.ClientGameState;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public final class ClientScenarioResultAssert {

    private final PlayerID player;
    private final ClientGameState state;
    private final List<Event> receivedEvents;
    private final PlayerSpawnManager spawnManager;


    public static ClientScenarioResultAssert assertThatClient(
            ScenarioResult scenarioResult, PlayerID player
    ) {
        ClientGameState state = scenarioResult.clientState(player);
        return new ClientScenarioResultAssert(
                player,
                state,
                scenarioResult.clientEvents(player),
                new PlayerSpawnManager(
                        player,
                        state.entityBoard(),
                        state.fogOfWar(),
                        state.claimedArea(),
                        state.resourceManager(),
                        state.terrain(),
                        state.turnManager()
                )
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

    public ClientScenarioResultAssert receivedNoEvents() {
        assertThat(receivedEvents).isEmpty();
        return this;
    }

    public ClientScenarioResultAssert turn(PlayerID player) {
        assertThat(state.turnManager().currentPlayer()).isEqualTo(player);
        return this;
    }

    public ClientScenarioResultAssert canCreateEntityExactlyOn(
            EntityType type, Position... positions
    ) {
        assertThat(spawnManager.allowedSpawnPositions(type)).containsExactly(positions);
        return this;
    }

    public ClientScenarioResultAssert cannotCreateEntityOn(EntityType type, Position... positions) {
        assertThat(spawnManager.allowedSpawnPositions(type)).doesNotContain(positions);
        return this;
    }

    public ClientScenarioResultAssert cannotCreate(EntityType type) {
        assertThat(spawnManager.allowedSpawnPositions(type)).isEmpty();
        return this;
    }

    public ClientScenarioResultAssert owns(Position... positions) {
        assertThat(positions).allMatch(
                p -> player.equals(state.claimedArea().owner(p).orElse(null))
        );
        return this;
    }

    public ClientScenarioResultAssert doesNotOwn(Position... positions) {
        assertThat(positions).noneMatch(
                p -> player.equals(state.claimedArea().owner(p).orElse(null))
        );
        return this;
    }

    public ClientScenarioResultAssert seesClaim(PlayerID expectedOwner, Position... positions) {
        assertThat(positions).allMatch(
                p -> expectedOwner.equals(state.claimedArea().owner(p).orElse(null))
        );
        return this;
    }

    public ClientScenarioResultAssert doesNotSeeClaim(
            PlayerID expectedOwner, Position... positions
    ) {
        assertThat(positions).noneMatch(
                p -> expectedOwner.equals(state.claimedArea().owner(p).orElse(null))
        );
        return this;
    }

    public ClientScenarioResultAssert has(int amount, ResourceType resourceType) {
        assertThat(state.resourceManager().amount(resourceType)).isEqualTo(amount);
        return this;
    }
}

