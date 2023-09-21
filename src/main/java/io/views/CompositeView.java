package io.views;

import io.model.View;
import io.model.engine.Canvas;
import io.model.engine.StateManager;
import io.model.engine.TextManager;
import io.model.engine.TextureBank;
import io.model.input.Input;

import java.util.List;

public class CompositeView implements View {
    private SimpleView view;
    private Input input;
    private TextureBank bank;
    private TextManager mgr;
    private StateManager stateManager;

    public CompositeView(SimpleView initialView) {
        initialView.parent = this;
        view = initialView;
    }

    void changeView(SimpleView newView) {
        newView.parent = this;
        view = newView;
        newView.update(new Input(List.of(), input.mouse(), input.window(), input.deltaTime()), bank, mgr, stateManager);
    }

    @Override
    public void draw(Canvas canvas) {
        view.draw(canvas);
    }

    @Override
    public void update(Input input, TextureBank bank, TextManager mgr, StateManager stateManager) {
        this.input = input;
        this.bank = bank;
        this.mgr = mgr;
        this.stateManager = stateManager;
        view.update(input, bank, mgr, stateManager);
    }
}
