package io.model.engine;

import io.model.textures.TextureDrawData;

public interface Canvas {
    void draw(TextureDrawData texture);

    void drawColored(TextureDrawData texture, float alpha, Color color);

    float getAspectRatio();
}
