package io.model.engine;

import mudgame.server.state.ServerState;

import java.util.Optional;
import java.util.concurrent.Future;

public interface StateManager {
    Future<Optional<ServerState>> loadState();
    void saveState(ServerState state);
}
