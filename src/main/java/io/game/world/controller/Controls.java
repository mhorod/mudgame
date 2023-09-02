package io.game.world.controller;

import core.model.EntityID;
import core.model.Position;

public interface Controls {
    void moveEntity(EntityID id, Position destination);

    void nextEvent();
}
