package io.game.world.controller;

import core.entities.EntityBoard;
import core.pathfinder.Pathfinder;
import core.terrain.Terrain;
import io.game.world.Map;
import core.event.Event;

import java.util.HashSet;

public record CommonState(
        Map map,
        Terrain terrain,
        EntityBoard entities,
        Pathfinder pathfinder,
        Controls controls,
        HashSet<Event> animatedEvents
) {
}
