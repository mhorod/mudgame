package io.game.world.controller;

import core.entities.EntityBoard;
import mudgame.events.Event;
import core.terrain.Terrain;
import io.game.world.Map;

import java.util.HashSet;

public record CommonState(
        Map map,
        Terrain terrain,
        EntityBoard entities,
        Controls controls,
        HashSet<Event> animatedEvents
) {
}
