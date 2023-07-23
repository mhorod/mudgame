package core;

import java.util.Collection;

public interface UnitsView
{
    record PlacedUnit(Unit unit, PlayerID owner, Position position) { }

    PlacedUnit getPlacedUnit(UnitID unitID);
    UnitID getUnitAtPosition(Position position);
    Collection<UnitID> getPlacedUnits();
}
