package middleware;

import java.util.Optional;

public interface Client {
    void processAllMessages();

    boolean hasCoreChanged();

    Optional<GameClient> getGameClient();
}
