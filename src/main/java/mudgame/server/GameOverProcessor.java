package mudgame.server;

import core.model.PlayerID;
import lombok.RequiredArgsConstructor;
import mudgame.controls.events.GameOver;
import mudgame.server.actions.EventSender;
import mudgame.server.internal.InteractiveState;

import java.util.List;

@RequiredArgsConstructor
public class GameOverProcessor {
    private final InteractiveState state;
    private final EventSender sender;

    public void checkGameOver() {
        state.winners().ifPresent(this::sendGameOver);
    }

    private void sendGameOver(List<PlayerID> winners) {
        sender.sendToEveryone(new GameOver(winners));
    }
}
