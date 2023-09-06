package io.game.world.tile;

import core.entities.model.Entity;

import java.util.List;

public record PlannedChange(TileKind kind, List<Entity> entities) {
}
