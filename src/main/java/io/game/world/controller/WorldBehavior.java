package io.game.world.controller;

import core.entities.events.HideEntity;
import core.entities.events.MoveEntity;
import core.entities.events.RemoveEntity;
import core.entities.events.ShowEntity;
import core.entities.events.SpawnEntity;
import core.model.EntityID;
import core.model.Position;
import core.terrain.events.SetTerrain;

public interface WorldBehavior {
    void onTileClick(Position position);

    void onEntityClick(EntityID entity);

    void onTileHover(Position position);

    void onEntityHover(EntityID entity);

    void onMoveEntity(MoveEntity event);

    void onSetTerrain(SetTerrain event);

    void onPlaceEntity(SpawnEntity event);

    void onRemoveEntity(RemoveEntity event);

    void onShowEntity(ShowEntity event);

    void onHideEntity(HideEntity event);
}
