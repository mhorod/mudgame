package mudgame.server.state;

public interface ServerStateSupplier {
    ServerState get(int playerCount);
}
