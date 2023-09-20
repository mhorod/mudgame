package testutils;

import core.model.PlayerID;

public class Players {
    public static PlayerID PLAYER_0 = player(0);
    public static PlayerID PLAYER_1 = player(1);
    public static PlayerID PLAYER_2 = player(2);
    public static PlayerID PLAYER_3 = player(3);

    public static PlayerID player(int id) { return new PlayerID(id); }
}
