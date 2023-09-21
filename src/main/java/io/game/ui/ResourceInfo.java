package io.game.ui;

import io.menu.Image;
import io.menu.Label;
import io.menu.Rectangle;
import io.menu.UIComponent;
import io.model.engine.Canvas;
import io.model.engine.TextManager;
import io.model.textures.Texture;

public class ResourceInfo implements UIComponent {
    private final Image bannerLeft = new Image(Texture.BANNER_LEFT);
    private final Image mud = new Image(Texture.MUD);
    private Label number;

    public ResourceInfo(int mudCount) {
        this.number = new Label(String.valueOf(mudCount));
    }

    public void setMudCount(int mudCount) {
        this.number = new Label(String.valueOf(mudCount));
    }

    @Override
    public float getAspectRatio(TextManager mgr) {
        return bannerLeft.getAspectRatio(mgr);
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        bannerLeft.fitInto(rectangle, mgr);
        var rect = this.bannerLeft.getBounds();
        mud.fitInto(new Rectangle(
                rect.position.x(),
                rect.position.y() + 0.01f,
                rect.width() * 0.7f,
                rect.height - 0.02f
        ), mgr);
        number.fitInto(new Rectangle(
                rect.position.x() + rect.width() * 0.3f,
                rect.position.y() + 0.01f,
                rect.width() / 2,
                rect.height - 0.02f
        ), mgr);
    }

    @Override
    public void draw(Canvas canvas) {
        bannerLeft.draw(canvas);
        mud.draw(canvas);
        number.draw(canvas);
    }
}
