package core;

import core.events.Event;
import core.events.EventObserver;
import core.events.EventSource;

import java.util.*;

public class SimpleUnitManager implements EventSource, UnitsView, UnitsController
{

    private record FogOfWarObserver(EventObserver observer, FogOfWarView fogOfWarView)
    {
        boolean isVisible(Position position)
        {
            return fogOfWarView().isVisible(position);
        }

        void receive(Event event) { observer().receive(event); }
    }

    private final List<EventObserver> observers = new LinkedList<>();
    private final List<FogOfWarObserver> fogOfWarObservers = new LinkedList<>();

    private final Map<UnitID, PlacedUnit> placedUnits = new HashMap<>();
    private final Map<Position, UnitID> unitAtPosition = new HashMap<>();

    int nextUnitID = 0;

    @Override
    public UnitID placeUnit(Event.PlaceUnit placeUnit)
    {
        UnitID id = newUnitID();

        placedUnits.put(id,
                        new PlacedUnit(placeUnit.unit(), placeUnit.owner(), placeUnit.position()));
        unitAtPosition.put(placeUnit.position(), id);

        Event event = new Event.UnitPlacement(placeUnit.unit(), id, placeUnit.owner(),
                                              placeUnit.position());

        broadcastEvent(event);
        sendIfSeesAnyPosition(event, List.of(placeUnit.position()));
        return id;
    }

    private void broadcastEvent(Event event)
    {
        for (EventObserver observer : observers)
            observer.receive(event);
    }

    private void sendIfSeesAnyPosition(Event event, List<Position> positions)
    {
        fogOfWarObservers.stream()
                .filter(observer -> positions.stream().anyMatch(observer::isVisible))
                .forEach(observer -> observer.observer.receive(event));
    }

    private UnitID newUnitID()
    {
        return new UnitID(this.nextUnitID++);
    }

    @Override
    public void removeUnit(Event.RemoveUnit removeUnit)
    {
        UnitID unitID = removeUnit.unitID();

        Position position = placedUnits.get(unitID).position();
        unitAtPosition.remove(position);
        placedUnits.remove(unitID);

        Event event = new Event.RemoveUnit(unitID);
        broadcastEvent(event);
        sendIfSeesAnyPosition(event, List.of(position));
    }

    @Override
    public void moveUnit(Event.MoveUnit moveUnit)
    {
        UnitID unitID = moveUnit.unitID();
        Position position = moveUnit.position();

        PlacedUnit placedUnit = placedUnits.get(unitID);
        unitAtPosition.remove(placedUnit.position());
        unitAtPosition.put(position, unitID);

        placedUnits.remove(unitID);
        placedUnits.put(unitID, new PlacedUnit(placedUnit.unit(), placedUnit.owner(), position));

        Event event = new Event.MoveUnit(unitID, position);
        broadcastEvent(event);
        sendIfSeesAnyPosition(event, List.of(placedUnit.position(), position));
    }

    @Override
    public PlacedUnit getPlacedUnit(UnitID unitID)
    {
        return placedUnits.get(unitID);
    }

    @Override
    public UnitID getUnitAtPosition(Position position)
    {
        return unitAtPosition.get(position);
    }

    @Override
    public Collection<UnitID> getPlacedUnits()
    {
        return placedUnits.keySet();
    }

    @Override
    public void addObserver(EventObserver observer)
    {
        observers.add(observer);
    }

    @Override
    public void addObserver(EventObserver observer, FogOfWarView fogOfWar)
    {
        fogOfWarObservers.add(new FogOfWarObserver(observer, fogOfWar));
    }
}
