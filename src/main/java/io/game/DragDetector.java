package io.game;

import io.model.ScreenPosition;
import io.model.input.MouseInfo;

public abstract class DragDetector {
    private ScreenPosition previous = new ScreenPosition(0, 0);
    private boolean dragging = false;

    public void update(MouseInfo mouse, float deltaTime) {
        if (dragging)
            onDrag(mouse.position().x() - previous.x(), mouse.position().y() - previous.y(), deltaTime);
        dragging = mouse.leftPressed();
        previous = mouse.position();
    }

    protected abstract void onDrag(float dx, float dy, float deltaTime);

}
