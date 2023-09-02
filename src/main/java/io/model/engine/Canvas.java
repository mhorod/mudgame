package io.model.engine;

import io.model.textures.TextureDrawData;

public interface Canvas {
    void draw(TextureDrawData texture);

    void drawTransparent(TextureDrawData texture, float alpha);

    float getAspectRatio();
}
