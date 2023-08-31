package io.model.input.events;

import io.model.ScreenPosition;

public record Click(ScreenPosition position) implements Event {
    @Override
    public void accept(EventHandler handler) {
        handler.onClick(this);
    }
}
