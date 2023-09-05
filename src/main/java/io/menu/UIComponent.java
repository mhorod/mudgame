package io.menu;

import io.model.Drawable;

public interface UIComponent extends Drawable {
    float getAspectRatio();

    void fitInto(Rectangle rectangle);
}
