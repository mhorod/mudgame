package core.events;

/**
 * Event is something that can happen and cause changes to the game
 */
public interface Event
{
    /**
     * Action is an event that can be caused directly by a player
     */
    interface Action extends Event { }

}

