package io.game.world.controller;

import core.entities.EntityBoard;
import core.event.Event;
import core.pathfinder.Pathfinder;
import core.terrain.model.Terrain;
import io.game.world.Map;

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
