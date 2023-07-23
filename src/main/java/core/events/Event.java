package core.events;

import core.PlayerID;
import core.Position;
import core.Unit;
import core.UnitID;

/**
 * Event is something that can happen and cause changes to the game
 */
public sealed interface Event
{
    sealed interface Action extends Event { }

    record PlaceUnit(Unit unit, PlayerID owner, Position position) implements Action { }

    record UnitPlacement(Unit unit, UnitID unitID, PlayerID owner, Position position)
            implements Event { }

    record RemoveUnit(UnitID unitID) implements Action { }

    record MoveUnit(UnitID unitID, Position position) implements Action { }
}
