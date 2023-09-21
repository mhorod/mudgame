package io.menu.buttons;

import io.menu.UIComponent;
import io.model.textures.Texture;

public class ButtonBig extends Button {
    public ButtonBig(UIComponent content) {
        super(content, Texture.BUTTON_BIG, Texture.BUTTON_BIG, Texture.BUTTON_BIG_PRESSED);
    }
}
