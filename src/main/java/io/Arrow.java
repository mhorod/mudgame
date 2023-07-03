package io;

public final class Arrow extends Top{
    public final Direction from;
    public final Direction to;
    public Arrow(ScreenPosition middle, Direction from, Direction to) {
        super(middle);
        this.from = from;
        this.to = to;
    }
}
