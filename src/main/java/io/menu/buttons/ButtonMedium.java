package io.menu.buttons;

import io.menu.UIComponent;
import io.model.textures.Texture;

public class ButtonMedium extends Button {
    public ButtonMedium(UIComponent content, Runnable onClick) {
        super(content, onClick, Texture.BUTTON_SMALL, Texture.BUTTON_SMALL, Texture.BUTTON_SMALL_PRESSED);
    }
}
