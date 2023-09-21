package middleware.utils;

import lombok.experimental.UtilityClass;
import middleware.server.GameServer;
import mudgame.server.state.ClassicServerStateSupplier;

@UtilityClass
public final class TestGameServer {
    public GameServer create() {
        return new GameServer(new ClassicServerStateSupplier());
    }
}
