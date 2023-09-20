package io.menu;

import io.model.Drawable;
import io.model.engine.TextManager;

public interface UIComponent extends Drawable {
    float getAspectRatio();

    void fitInto(Rectangle rectangle, TextManager mgr);
}
