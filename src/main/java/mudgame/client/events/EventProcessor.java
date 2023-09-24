package mudgame.client.events;

import mudgame.client.ClientGameState;
import mudgame.controls.events.AttackEntityEvent;
import mudgame.controls.events.AttackPosition;
import mudgame.controls.events.ChargeResources;
import mudgame.controls.events.ClaimChanges;
import mudgame.controls.events.DamageEntity;
import mudgame.controls.events.Event;
import mudgame.controls.events.GameOver;
import mudgame.controls.events.KillEntity;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.NextTurn;
import mudgame.controls.events.PlaceEntity;
import mudgame.controls.events.ProduceResources;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.SpawnEntity;

public final class EventProcessor {
    private final ClientGameState state;
    private final EntityEventProcessor entityEventProcessor;
    private final ResourceEventProcessor resourceEventProcessor;
    private final CompleteTurnProcessor completeTurnProcessor;
    private final ClaimEventProcessor claimEventProcessor;

    public EventProcessor(ClientGameState state) {
        this.state = state;
        entityEventProcessor = new EntityEventProcessor(state);
        resourceEventProcessor = new ResourceEventProcessor(state);
        completeTurnProcessor = new CompleteTurnProcessor(state);
        claimEventProcessor = new ClaimEventProcessor(state);
    }

    public void process(Event event) {
        if (event instanceof SpawnEntity e)
            entityEventProcessor.spawnEntity(e);
        else if (event instanceof PlaceEntity e)
            entityEventProcessor.placeEntity(e);
        else if (event instanceof RemoveEntity e)
            entityEventProcessor.removeEntity(e);
        else if (event instanceof MoveEntityAlongPath e)
            entityEventProcessor.moveEntityAlongPath(e);
        else if (event instanceof NextTurn e)
            completeTurnProcessor.nextTurn(e);
        else if (event instanceof GameOver e)
            gameOver(e);
        else if (event instanceof AttackEntityEvent e)
            entityEventProcessor.attackEntity(e);
        else if (event instanceof DamageEntity e)
            entityEventProcessor.damageEntity(e);
        else if (event instanceof AttackPosition e)
            entityEventProcessor.attackPosition(e);
        else if (event instanceof KillEntity e)
            entityEventProcessor.killEntity(e);
        else if (event instanceof ProduceResources e)
            resourceEventProcessor.produceResources(e);
        else if (event instanceof ChargeResources e)
            resourceEventProcessor.chargeResources(e);
        else if (event instanceof ClaimChanges e)
            claimEventProcessor.claimChange(e);
    }


    private void gameOver(GameOver e) {
        state.gameOverCondition().gameOver(e.winners());
    }
}
