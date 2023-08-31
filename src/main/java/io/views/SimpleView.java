package io.views;

import io.model.View;

public abstract class SimpleView implements View {
    CompositeView parent;

    protected final void changeView(SimpleView next) {
        if (parent == null) throw new RuntimeException("SimpleView without parent tried to change.");
        parent.changeView(next);
    }

}
