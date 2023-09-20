package io.game.world.controller;

import core.entities.EntityBoardView;
import mudgame.controls.events.Event;
import core.pathfinder.Pathfinder;
import core.spawning.PlayerSpawnManager;
import core.terrain.TerrainView;
import io.game.world.Map;

import java.util.HashSet;

public record CommonState(
        Map map,
        TerrainView terrain,
        EntityBoardView entities,
        Pathfinder pathfinder,
        PlayerSpawnManager spawnManager,
        Controls controls,
        HashSet<Event> animatedEvents
) {
}
