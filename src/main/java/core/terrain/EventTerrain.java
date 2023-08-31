package core.terrain;

import core.events.Event;
import core.events.EventObserver;
import core.fogofwar.events.SetVisibility;
import core.fogofwar.events.SetVisibility.SetPositionVisibility;
import core.terrain.events.SetTerrain;
import core.terrain.events.SetTerrain.SetPositionTerrain;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static core.terrain.model.TerrainType.UNKNOWN;

@RequiredArgsConstructor
public class EventTerrain implements EventObserver {
    private final Terrain terrain;
    private final EventObserver eventObserver;

    @Override
    public void receive(Event event) {
        if (event instanceof SetVisibility e)
            setVisibility(e);
        else if (event instanceof SetTerrain e)
            setTerrain(e);
    }

    private void setTerrain(SetTerrain e) {
        for (SetPositionTerrain p : e.positions())
            terrain.setTerrainAt(p.position(), p.terrainType());
    }

    private void setVisibility(SetVisibility e) {
        List<SetPositionTerrain> terrainPositions = new ArrayList<>();
        for (SetPositionVisibility p : e.postions())
            terrainPositions.add(intoTerrainChange(p));
        eventObserver.receive(new SetTerrain(terrainPositions));
    }

    private SetPositionTerrain intoTerrainChange(SetPositionVisibility p) {
        if (p.isVisible())
            return new SetPositionTerrain(p.position(), terrain.terrainAt(p.position()));
        else
            return new SetPositionTerrain(p.position(), UNKNOWN);
    }
}
