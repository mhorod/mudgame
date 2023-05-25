package core;

import java.util.List;

public interface Event
{
    // Events may not always be visible to all players (e.g. due to the fog of war)
    List<Game.PlayerID> getRecipients();
}
