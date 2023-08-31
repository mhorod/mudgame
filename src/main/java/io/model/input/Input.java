package io.model.input;

import io.model.input.events.Event;

import java.util.List;

public record Input(List<Event> events, MouseInfo mouse, WindowInfo window, float deltaTime) {
}
