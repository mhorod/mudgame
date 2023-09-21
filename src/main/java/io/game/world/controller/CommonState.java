package io.game.world.controller;

import core.entities.EntityBoardView;
import core.model.PlayerID;
import core.pathfinder.Pathfinder;
import core.spawning.PlayerSpawnManager;
import core.terrain.TerrainView;
import io.game.ui.HUD;
import io.game.world.Map;
import mudgame.client.PlayerAttackManager;
import mudgame.controls.Controls;
import mudgame.controls.events.Event;

import java.util.HashSet;

public record CommonState(
        Map map,
        HUD hud,
        PlayerID myID,
        TerrainView terrain,
        EntityBoardView entities,
        Pathfinder pathfinder,
        PlayerSpawnManager spawnManager,
        PlayerAttackManager attackManager,
        Controls controls,
        HashSet<Event> animatedEvents
) {
}
