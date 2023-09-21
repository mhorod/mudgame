package io.menu.containers;

import io.menu.Rectangle;
import io.menu.UIComponent;
import io.model.engine.Canvas;
import io.model.engine.TextManager;

import java.util.List;

public class HBox implements UIComponent {
    private final float gap;
    List<? extends UIComponent> components;

    public HBox(float gap, List<? extends UIComponent> components) {
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
        for (UIComponent component : components) aspect += 1 / component.getAspectRatio(mgr) + gap;
        System.out.println(aspect);
        return 1 / aspect;
    }

    @Override
    public void fitInto(Rectangle rect, TextManager mgr) {
        Rectangle bounds = new Rectangle(getAspectRatio(mgr));
        bounds.fitInto(rect);
        float x = bounds.position.x();
        for (var component : components) {
            var componentBounds = new Rectangle(
                    x,
                    bounds.position.y(),
                    bounds.height / component.getAspectRatio(mgr),
                    bounds.height
            );
            x += bounds.height / component.getAspectRatio(mgr) + gap * bounds.height;
            component.fitInto(componentBounds, mgr);

        }
    }

}
