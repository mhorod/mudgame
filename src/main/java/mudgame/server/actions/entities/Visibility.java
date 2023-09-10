package mudgame.server.actions.entities;

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


@RequiredArgsConstructor
final class Visibility {
    private final EntityBoardView entityBoard;
    private final TerrainView terrain;
    private final ClaimedAreaView claimedArea;

    VisibilityChange convert(Set<PositionVisibility> positions) {
        return new VisibilityChange(
                positions.stream()
                        .filter(p -> terrain.contains(p.position()))
                        .map(this::convert).toList()
        );
    }

    private PositionVisibilityChange convert(PositionVisibility pv) {
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
