package io.model;

import io.model.engine.TextManager;
import io.model.engine.TextureBank;
import io.model.input.Input;

public interface View extends Drawable {
    void update(Input input, TextureBank bank, TextManager mgr);
}
