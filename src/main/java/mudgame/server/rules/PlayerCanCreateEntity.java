package mudgame.server.rules;

import core.event.Action;
import core.model.PlayerID;
import core.spawning.SpawnManager;
import lombok.RequiredArgsConstructor;
import mudgame.controls.actions.CreateEntity;

@RequiredArgsConstructor
public class PlayerCanCreateEntity implements ActionRule {
    private final SpawnManager spawnManager;


    @Override
    public boolean isSatisfied(Action action, PlayerID actor) {
        if (action instanceof CreateEntity a)
            return spawnManager.canSpawn(actor, a.type(), a.position());
        else
            return true;
    }
}
