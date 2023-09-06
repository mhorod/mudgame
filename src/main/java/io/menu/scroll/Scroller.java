package io.menu.scroll;

import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.textures.Texture;
import io.model.textures.TextureDrawData;

public class Scroller {
    public float width, height;
    public ScreenPosition position;
    private final Texture top, mid, bot;

    public Scroller(boolean back) {
        if (back) {
            top = Texture.SCROLL_BACK_TOP;
            mid = Texture.SCROLL_BACK_MID;
            bot = Texture.SCROLL_BACK_BOT;
        } else {
            top = Texture.SCROLL_TOP;
            mid = Texture.SCROLL_MID;
            bot = Texture.SCROLL_BOT;
        }
    }

    public void draw(Canvas canvas) {
        for (float y = 0; y < height - mid.aspectRatio() * width; y += mid.aspectRatio() * width)
            canvas.draw(new TextureDrawData(
                    mid,
                    new ScreenPosition(position.x(), position.y() + y),
                    mid.aspectRatio() * width
            ));

        canvas.draw(new TextureDrawData(
                bot,
                position,
                bot.aspectRatio() * width
        ));
        canvas.draw(new TextureDrawData(
                top,
                new ScreenPosition(position.x(), position.y() + height - top.aspectRatio() * width),
                top.aspectRatio() * width
        ));
    }
}
