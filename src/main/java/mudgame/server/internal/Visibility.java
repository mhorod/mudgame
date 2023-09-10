package mudgame.server.internal;

import core.claiming.ClaimedAreaView;
import core.entities.EntityBoardView;
import core.fogofwar.PlayerFogOfWar.PositionVisibility;
import core.terrain.TerrainView;
import lombok.RequiredArgsConstructor;
import mudgame.controls.events.VisibilityChange;
import mudgame.controls.events.VisibilityChange.HidePosition;
import mudgame.controls.events.VisibilityChange.PositionVisibilityChange;
import mudgame.controls.events.VisibilityChange.ShowPosition;

import java.util.Set;


/**
 * Collects information received when visibility of fields changes.
 */
@RequiredArgsConstructor
final class Visibility {
    private final TerrainView terrain;
    private final EntityBoardView entityBoard;
    private final ClaimedAreaView claimedArea;


    VisibilityChange get(Set<PositionVisibility> positions) {
        return new VisibilityChange(
                positions.stream()
                        .filter(p -> terrain.contains(p.position()))
                        .map(this::get).toList()
        );
    }

    private PositionVisibilityChange get(PositionVisibility pv) {
        if (!pv.isVisible())
            return new HidePosition(pv.position());
        else {
            return new ShowPosition(
                    pv.position(),
                    terrain.terrainAt(pv.position()),
                    entityBoard.entitiesAt(pv.position()),
                    claimedArea.owner(pv.position()).orElse(null)
            );
        }

    }
}
