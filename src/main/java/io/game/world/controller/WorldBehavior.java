package io.game.world.controller;

import core.model.EntityID;
import core.model.Position;
import core.terrain.events.SetTerrain;
import mudgame.controls.events.HideEntity;
import mudgame.controls.events.MoveEntityAlongPath;
import mudgame.controls.events.RemoveEntity;
import mudgame.controls.events.ShowEntity;
import mudgame.controls.events.SpawnEntity;

public interface WorldBehavior {
    void onTileClick(Position position);

    void onEntityClick(EntityID entity);

    void onTileHover(Position position);

    void onEntityHover(EntityID entity);

    void onSetTerrain(SetTerrain event);

    void onPlaceEntity(SpawnEntity event);

    void onRemoveEntity(RemoveEntity event);

    void onShowEntity(ShowEntity event);

    void onHideEntity(HideEntity event);

    void onMoveEntityAlongPath(MoveEntityAlongPath e);
}
