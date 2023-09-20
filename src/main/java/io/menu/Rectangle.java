package io.menu;

import io.model.ScreenPosition;

public class Rectangle {
    private final float aspectRatio;
    public ScreenPosition position;
    public float height;

    public Rectangle(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Rectangle(float x, float y, float width, float height) {
        this.position = new ScreenPosition(x, y);
        this.aspectRatio = height / width;
        this.height = height;
    }

    public float width() {
        return height / aspectRatio;
    }

    public void fitInto(Rectangle rect) {
        this.height = Math.min(rect.height, rect.width() * aspectRatio);
        this.position = new ScreenPosition(
                rect.position.x() + rect.width() / 2 - width() / 2,
                rect.position.y() + rect.height / 2 - height / 2
        );
    }

    public boolean contains(ScreenPosition position) {
        return position.x() > this.position.x()
                && position.y() > this.position.y()
                && position.y() < this.position.y() + height
                && position.x() < this.position.x() + height / aspectRatio;
    }

}
