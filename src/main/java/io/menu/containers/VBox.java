package io.menu.containers;

import io.menu.Rectangle;
import io.menu.UIComponent;
import io.model.engine.Canvas;
import io.model.engine.TextManager;

import java.util.List;

public class VBox implements UIComponent {
    private final float gap;
    List<? extends UIComponent> components;

    public VBox(float gap, List<? extends UIComponent> components) {
        this.gap = gap;
        this.components = components;
    }

    @Override
    public void draw(Canvas canvas) {
        components.forEach(component -> component.draw(canvas));
    }


    @Override
    public float getAspectRatio(TextManager mgr) {
        float aspect = -gap;
        for (UIComponent component : components) aspect += component.getAspectRatio(mgr) + gap;
        return aspect;
    }

    @Override
    public void fitInto(Rectangle rect, TextManager mgr) {
        Rectangle bounds = new Rectangle(getAspectRatio(mgr));
        bounds.fitInto(rect);
        float y = bounds.position.y();
        for (var component : components) {
            var componentBounds = new Rectangle(
                    bounds.position.x(),
                    y,
                    bounds.width(),
                    bounds.width() * component.getAspectRatio(mgr)
            );
            y += bounds.width() * component.getAspectRatio(mgr) + gap * bounds.width();
            component.fitInto(componentBounds, mgr);

        }
    }

}
