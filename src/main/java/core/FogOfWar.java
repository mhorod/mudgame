package core;

import core.id.PlayerID;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FogOfWar
{
    private final Map<PlayerID, PlayerFogOfWar> fows;

    public FogOfWar(List<PlayerID> players, Supplier<PlayerFogOfWar> fogOfWarSupplier)
    {
        fows = players.stream().collect(Collectors.toMap(p -> p, p -> fogOfWarSupplier.get()));
    }

    boolean isVisible(Position position, PlayerID viewer)
    {
        return fows.get(viewer).isVisible(position);
    }

    public void setVisibility(Position position, PlayerID viewer, boolean isVisible)
    {
        fows.get(viewer).setVisibility(position, isVisible);
    }
}
