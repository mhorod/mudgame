package io.views;

import io.model.View;
import io.model.engine.Canvas;
import io.model.engine.TextureBank;
import io.model.input.Input;

public class CompositeView implements View {
    private SimpleView view;

    public CompositeView(SimpleView initialView) {
        initialView.parent = this;
        view = initialView;
    }

    void changeView(SimpleView newView) {
        newView.parent = this;
        view = newView;
    }

    @Override
    public void draw(Canvas canvas) {
        view.draw(canvas);
    }

    @Override
    public void update(Input input, TextureBank bank) {
        view.update(input, bank);
    }
}
