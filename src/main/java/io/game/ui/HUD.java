package io.game.ui;

import core.entities.model.EntityType;
import io.menu.Image;
import io.menu.Rectangle;
import io.menu.UIComponent;
import io.menu.buttons.Button;
import io.menu.buttons.ButtonSmall;
import io.menu.containers.HBox;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextManager;
import io.model.textures.Texture;

import java.util.List;

public class HUD {
    Button pawn = new ButtonSmall(new Image(Texture.PAWN));
    Button warrior = new ButtonSmall(new Image(Texture.WARRIOR));
    Button marshWiggle = new ButtonSmall(new Image(Texture.MARSH_WIGGLE));
    Button base = new ButtonSmall(new Image(Texture.BASE));
    Button tower = new ButtonSmall(new Image(Texture.TOWER));

    List<Button> buttons = List.of(pawn, warrior, marshWiggle, base, tower);

    UIComponent content = new HBox(0.1f, buttons);

    public void update(ScreenPosition mouse, boolean pressed, TextManager mgr) {
        content.fitInto(new Rectangle(0, 0.01f, 1, 0.1f), mgr);
        buttons.forEach(button -> button.update(mouse, pressed));
    }

    public void draw(Canvas canvas) {
        content.draw(canvas);
    }

    public boolean click(ScreenPosition position, HUDListener listener) {
        if (pawn.contains(position))
            listener.onEntityTypeSelected(EntityType.PAWN);
        else if (warrior.contains(position))
            listener.onEntityTypeSelected(EntityType.WARRIOR);
        else if (marshWiggle.contains(position))
            listener.onEntityTypeSelected(EntityType.MARSH_WIGGLE);
        else if (base.contains(position))
            listener.onEntityTypeSelected(EntityType.BASE);
        else if (tower.contains(position))
            listener.onEntityTypeSelected(EntityType.TOWER);
        else return false;
        return true;
    }

    public void setPressed(EntityType type) {
        switch (type) {
            case BASE -> base.setPressed(true);
            case PAWN -> pawn.setPressed(true);
            case TOWER -> tower.setPressed(true);
            case WARRIOR -> warrior.setPressed(true);
            case MARSH_WIGGLE -> marshWiggle.setPressed(true);
        }
    }

    public void clear() {
        for (var button : buttons)
            button.setPressed(false);
    }

}
