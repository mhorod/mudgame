package io.model.engine;

import io.model.textures.TextureDrawData;

public interface Canvas {
    void draw(TextureDrawData texture);

    float getAspectRatio();
}
