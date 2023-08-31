package core.server;

import core.entities.EntityBoard;
import core.fogofwar.FogOfWar;
import core.terrain.Terrain;
import core.turns.PlayerManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class ServerStateBuilder {
    private PlayerManager playerManager;
    private EntityBoard entityBoard;
    private FogOfWar fogOfWar;
    private Terrain terrain;

    static ServerStateBuilder from(ServerGameState state) {
        return new ServerStateBuilder(
                state.playerManager(),
                state.entityBoard(),
                state.fogOfWar(),
                state.terrain()
        );
    }

    ServerStateBuilder withFogOfWar(FogOfWar fogOfWar) {
        this.fogOfWar = fogOfWar;
        return this;
    }

    ServerGameState build() {
        return new ServerGameState(
                playerManager,
                entityBoard,
                fogOfWar,
                terrain,
                ServerCore.defaultRules(playerManager, entityBoard, fogOfWar)
        );
    }
}
