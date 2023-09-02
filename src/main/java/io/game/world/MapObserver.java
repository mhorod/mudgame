package io.game.world;

import core.model.EntityID;
import core.model.Position;

public interface MapObserver {
    void onEntity(EntityID id);

    void onTile(Position position);
}
