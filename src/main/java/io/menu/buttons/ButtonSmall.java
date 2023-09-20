package io.menu.buttons;

import io.menu.UIComponent;
import io.model.textures.Texture;

public class ButtonSmall extends Button {
    public ButtonSmall(UIComponent content, Runnable onClick) {
        super(content, onClick, Texture.BUTTON_TINY, Texture.BUTTON_TINY, Texture.BUTTON_TINY_PRESSED);
    }
}
