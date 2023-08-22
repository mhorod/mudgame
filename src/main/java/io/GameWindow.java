package io;

public class GameWindow implements GameUI {
    private final MapView mapView = new MapView(100, 100, 0.07f, new ScreenPosition(0.5f, 0.5f));

    float scrollVelocityX = 0;
    float scrollVelocityY = 0;
    boolean mouseDown = false;
    float targetScale = mapView.scale;
    ScreenPosition pivot;

    ScreenPosition mouse = new ScreenPosition(0, 0);

    @Override
    public void draw(Drawer d) {
        mapView.draw(d);
    }

    @Override
    public void update() {
        if (mapView.scale != targetScale) {
            if (Math.max(targetScale, mapView.scale) / Math.min(targetScale, mapView.scale) < 1.001)
                mapView.setScale(pivot, targetScale);
            else
                mapView.setScale(pivot, mapView.scale + (targetScale - mapView.scale) * 0.15f);
        }
        if (!mouseDown) {
            mapView.offset = new ScreenPosition(mapView.offset.x() + scrollVelocityX,
                                                mapView.offset.y() + scrollVelocityY);
            scrollVelocityX *= 0.95;
            scrollVelocityY *= 0.95;
            if (scrollVelocityX * scrollVelocityX + scrollVelocityY * scrollVelocityY < 0.0000001) {
                scrollVelocityX = 0;
                scrollVelocityY = 0;
            }
        }
        else {
            scrollVelocityX = 0;
            scrollVelocityY = 0;
        }

        mapView.update(mouse);
    }

    @Override
    public void resize(int new_width, int new_height) { }

    @Override
    public void mouseClick(ScreenPosition pos) { }

    @Override
    public void mousePress(ScreenPosition pos) {
        mouseDown = true;
    }

    @Override
    public void mouseMove(ScreenPosition pos) {
        mouse = pos;
    }

    @Override
    public void mouseRelease(ScreenPosition pos) {
        mouseDown = false;
    }

    @Override
    public void mouseDragged(ScreenPosition pos1, ScreenPosition pos2) {
        scrollVelocityX = pos2.x() - pos1.x();
        scrollVelocityY = pos2.y() - pos1.y();
        mouse = pos2;
        mapView.offset = new ScreenPosition(mapView.offset.x() + pos2.x() - pos1.x(),
                                            mapView.offset.y() + pos2.y() - pos1.y());
    }

    @Override
    public void mouseScroll(float amount, ScreenPosition pos) {
        pivot = pos;
        targetScale *= Math.pow(1.1f, -amount);
    }
}
