package io;

public class GameWindow implements GameUI {
    private final MapView mapView = new MapView(100, 100, 0.07f, new ScreenPosition(0.5f, 0.5f));

    @Override
    public void draw(Drawer d) {
        mapView.draw(d);
    }
    @Override
    public void update() { }

    @Override
    public void resize(int new_width, int new_height) { }

    @Override
    public void mouseClick(ScreenPosition pos) { }

    @Override
    public void mousePress(ScreenPosition pos) { }

    @Override
    public void mouseDragged(ScreenPosition pos1, ScreenPosition pos2) {
        mapView.offset = new ScreenPosition(
                mapView.offset.x() + pos2.x() - pos1.x(),
                mapView.offset.y() + pos2.y() - pos1.y()
        );
    }
}
