package io;

public interface GameUI extends Drawable {

    void update();
    void resize(int new_width, int new_height);
    void mouseClick(ScreenPosition pos);
    void mousePress(ScreenPosition pos);
    void mouseDragged(ScreenPosition pos1, ScreenPosition pos2);
}
