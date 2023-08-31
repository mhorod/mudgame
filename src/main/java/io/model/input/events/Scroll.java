package io.model.input.events;

public record Scroll(float amount) implements Event {
    @Override
    public void accept(EventHandler handler) {
        handler.onScroll(this);
    }
}
