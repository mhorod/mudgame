package io.model.textures;

import io.model.ScreenPosition;

public record TextureDrawData(Texture texture, ScreenPosition position, float height) {
}
