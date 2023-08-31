package io.model.engine;

import io.model.ScreenPosition;
import io.model.textures.TextureDrawData;

public interface TextureBank {
    boolean contains(TextureDrawData texture, ScreenPosition pos);
}
