package io.model.engine;

import io.model.ScreenPosition;
import io.model.textures.TextureDrawData;

public interface Canvas {
    void draw(TextureDrawData texture);

    void drawColored(TextureDrawData texture, float alpha, Color color);

    void drawText(String text, ScreenPosition position, float height);

    float getTextAspectRatio(String text);

    float getAspectRatio();
}
