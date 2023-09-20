package io.menu.buttons;

import io.menu.UIComponent;
import io.model.textures.Texture;

public class ButtonBig extends Button {
    public ButtonBig(UIComponent content, Runnable onClick) {
        super(content, onClick, Texture.BUTTON_BIG, Texture.BUTTON_BIG, Texture.BUTTON_BIG_PRESSED);
    }
}
