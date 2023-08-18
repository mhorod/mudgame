package core;

import core.model.PlayerID;

public record PlayerAction<T>(PlayerID actor, T action)
{
    public static <T> PlayerAction<T> from(long actor, T action)
    {
        return new PlayerAction<>(new PlayerID(actor), action);
    }
}
