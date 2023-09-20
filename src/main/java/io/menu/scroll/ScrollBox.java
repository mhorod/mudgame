package io.menu.scroll;

import io.menu.Rectangle;
import io.menu.UIComponent;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.TextManager;

public class ScrollBox implements UIComponent {
    private final UIComponent contents;
    private final Scroller scrollBack = new Scroller(true);
    private final Scroller scrollFront = new Scroller(false);

    private float scrollAmount;
    private float maxScroll;

    public ScrollBox(UIComponent contents) {
        this.contents = contents;
    }

    @Override
    public float getAspectRatio() {
        return 1f;
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        var contentWidth = rectangle.width() * 0.9f;
        var contentHeight = contents.getAspectRatio() * contentWidth;
        maxScroll = Math.max((contentHeight - rectangle.height) / contentWidth, 0);
        if (scrollAmount > maxScroll)
            scrollAmount = maxScroll;
        var contentBounds = new Rectangle(
                rectangle.position.x(),
                rectangle.position.y() + rectangle.height - contentHeight + scrollAmount * contentWidth,
                contentWidth,
                contentHeight
        );
        scrollBack.position = new ScreenPosition(
                rectangle.position.x() + rectangle.width() * 0.91f,
                rectangle.position.y()
        );
        scrollBack.height = rectangle.height;
        scrollBack.width = scrollFront.width = rectangle.width() * 0.08f;
        scrollFront.height = scrollBack.height * Math.min(rectangle.height / contentHeight, 1f);
        scrollFront.position = new ScreenPosition(
                rectangle.position.x() + rectangle.width() * 0.91f,
                rectangle.position.y() + scrollBack.height - scrollFront.height - scrollAmount * contentWidth / contentHeight * scrollBack.height
        );
        contents.fitInto(contentBounds, mgr);
    }

    public void setScroll(float scroll) {
        scrollAmount = scroll;
        if (scrollAmount < 0)
            scrollAmount = 0;
        if (scrollAmount > maxScroll)
            scrollAmount = maxScroll;
    }

    public float getMaxScroll() {
        return maxScroll;
    }

    @Override
    public void draw(Canvas canvas) {
        contents.draw(canvas);
        scrollBack.draw(canvas);
        scrollFront.draw(canvas);
    }
}
