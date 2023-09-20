package io.game.world.controller;

import core.model.EntityID;
import core.model.Position;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.SpawnEntity;
import mudgame.controls.events.VisibilityChange;

public interface WorldBehavior {
    void onTileClick(Position position);

    void onEntityClick(EntityID entity);

    void onTileHover(Position position);

    void onEntityHover(EntityID entity);

    void onVisibilityChange(VisibilityChange event);

    void onSpawnEntity(SpawnEntity event);

    void onRemoveEntity(RemoveEntity event);


    void onMoveEntityAlongPath(MoveEntityAlongPath e);
}
