package core.terrain;

import core.events.Event;
import core.events.EventObserver;
import core.events.EventOccurrence;
import core.events.EventOccurrenceObserver;
import core.fogofwar.events.SetVisibility;
import core.fogofwar.events.SetVisibility.SetPositionVisibility;
import core.terrain.events.SetTerrain;
import core.terrain.events.SetTerrain.SetPositionTerrain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static core.terrain.model.TerrainType.UNKNOWN;

@Slf4j
@RequiredArgsConstructor
public class EventTerrain implements EventOccurrenceObserver, EventObserver {
    private final Terrain terrain;
    private final EventOccurrenceObserver observer;

    private void setTerrain(SetTerrain e) {
        for (SetPositionTerrain p : e.positions())
            terrain.setTerrainAt(p.position(), p.terrainType());
    }

    private SetTerrain setVisibility(SetVisibility e) {
        List<SetPositionTerrain> terrainPositions = new ArrayList<>();
        for (SetPositionVisibility p : e.positions())
            if (terrain.contains(p.position()))
                terrainPositions.add(intoTerrainChange(p));
        return new SetTerrain(terrainPositions);
    }

    private SetPositionTerrain intoTerrainChange(SetPositionVisibility p) {
        if (p.isVisible())
            return new SetPositionTerrain(p.position(), terrain.terrainAt(p.position()));
        else
            return new SetPositionTerrain(p.position(), UNKNOWN);
    }

    @Override
    public void receive(EventOccurrence eventOccurrence) {
        log.info("Received event occurrence: {}", eventOccurrence);
        Event event = eventOccurrence.event();
        if (event instanceof SetVisibility e)
            observer.receive(new EventOccurrence(setVisibility(e), eventOccurrence.recipients()));
    }

    @Override
    public void receive(Event event) {
        log.info("Received event: {}", event);
        if (event instanceof SetTerrain e)
            setTerrain(e);
    }
}
