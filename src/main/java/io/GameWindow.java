package io;

public class GameWindow implements GameUI {
    private final MapView mapView = new MapView(10, 10, 0.07f, new ScreenPosition(0.5f, 0.5f));

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
}
