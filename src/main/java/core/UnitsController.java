package core;

import core.events.Event;

public interface UnitsController
{
    UnitID placeUnit(Event.PlaceUnit placeUnit);
    void removeUnit(Event.RemoveUnit removeUnit);
    void moveUnit(Event.MoveUnit moveUnit);
}
