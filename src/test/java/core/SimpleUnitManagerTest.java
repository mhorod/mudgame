package core;

import core.events.Event;
import core.events.EventObserver;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SimpleUnitManagerTest
{
    @Test
    void new_unit_manager_contains_no_units()
    {
        // given
        SimpleUnitManager unitManager = new SimpleUnitManager();

        // then
        assertTrue(unitManager.getPlacedUnits().isEmpty());
    }

    @Test
    void unit_manager_sends_place_event_to_observers()
    {
        // given
        SimpleUnitManager unitManager = new SimpleUnitManager();
        EventObserver observer = mock(EventObserver.class);
        unitManager.addObserver(observer);

        Unit unit = mock(Unit.class);
        PlayerID owner = new PlayerID(0);
        Position position = new Position(0, 0);
        Event.PlaceUnit action = new Event.PlaceUnit(unit, owner, position);

        // when
        unitManager.placeUnit(action);

        // then
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(observer).receive(captor.capture());
        assertEquals(Event.UnitPlacement.class, captor.getValue().getClass());
        Event.UnitPlacement receivedEvent = (Event.UnitPlacement) captor.getValue();
        assertEquals(unit, receivedEvent.unit());
        assertEquals(owner, receivedEvent.owner());
        assertEquals(position, receivedEvent.position());
    }
}
