package io;

public sealed class Top permits Arrow {
    public final ScreenPosition middle;

    protected Top(ScreenPosition middle) {
        this.middle = middle;
    }
}
