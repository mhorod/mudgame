package io.game.world.controller;

import core.entities.events.HideEntity;
import core.entities.events.MoveEntity;
import core.entities.events.PlaceEntity;
import core.entities.events.RemoveEntity;
import core.entities.events.ShowEntity;
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

    void onPlaceEntity(PlaceEntity event);

    void onRemoveEntity(RemoveEntity event);

    void onShowEntity(ShowEntity event);

    void onHideEntity(HideEntity event);
}
