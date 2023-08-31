package io.model.input.events;

public sealed interface Event permits Click, Scroll {
    void accept(EventHandler handler);
}
