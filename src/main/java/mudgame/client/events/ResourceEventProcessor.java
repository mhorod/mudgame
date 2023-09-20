package mudgame.client.events;

import lombok.RequiredArgsConstructor;
import mudgame.client.ClientGameState;
import mudgame.controls.events.ChargeResources;
import mudgame.controls.events.ProduceResources;

@RequiredArgsConstructor
class ResourceEventProcessor {
    private final ClientGameState state;

    public void produceResources(ProduceResources e) {
        state.resourceManager().add(e.resources());
    }

    public void chargeResources(ChargeResources e) {
        state.resourceManager().subtract(e.resources());
    }
}
