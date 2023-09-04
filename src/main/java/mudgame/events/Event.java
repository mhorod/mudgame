package mudgame.events;

import java.io.Serializable;

/**
 * Event is something that can happen and cause changes to the game
 */
public interface Event extends Serializable {
    /**
     * Action is an event that can be caused directly by a player
     */
    interface Action extends Event { }

}

