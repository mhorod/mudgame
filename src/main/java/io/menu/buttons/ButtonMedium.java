package io.menu.buttons;

import io.menu.UIComponent;
import io.model.textures.Texture;

public class ButtonMedium extends Button {
    public ButtonMedium(UIComponent content) {
        super(content, Texture.BUTTON_SMALL, Texture.BUTTON_SMALL, Texture.BUTTON_SMALL_PRESSED);
    }
}
