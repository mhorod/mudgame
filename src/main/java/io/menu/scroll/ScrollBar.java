package io.menu.scroll;

import io.menu.Rectangle;
import io.menu.UIComponent;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextManager;

public class ScrollBar implements UIComponent {
    private final Scroller scrollBack = new Scroller(true);
    private final Scroller scrollFront = new Scroller(false);

    public float maxScroll;
    public float scrollerSize;
    public float scrollAmount;

    public ScrollBar(float maxScroll, float scrollerSize, float scrollAmount) {
        this.maxScroll = maxScroll;
        this.scrollerSize = scrollerSize;
        this.scrollAmount = scrollAmount;
    }

    @Override
    public float getAspectRatio(TextManager mgr) {
        return 0;
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        scrollBack.position = new ScreenPosition(
                rectangle.position.x(),
                rectangle.position.y()
        );
        scrollBack.height = rectangle.height;
        scrollBack.width = scrollFront.width = rectangle.width();
        scrollFront.height = scrollBack.height * scrollerSize;
        scrollFront.position = new ScreenPosition(
                rectangle.position.x(),
                rectangle.position.y() + scrollBack.height - scrollFront.height - (1 - scrollAmount / maxScroll) * (scrollBack.height - scrollFront.height)
        );
    }

    @Override
    public void draw(Canvas canvas) {
        scrollBack.draw(canvas);
        scrollFront.draw(canvas);
    }
}
