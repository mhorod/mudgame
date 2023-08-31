package io.model.input.events;

public interface EventHandler {
    void onClick(Click click);

    void onScroll(Scroll scroll);
}
