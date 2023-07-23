package core.events;

import core.FogOfWarView;

public interface EventSource
{
    void addObserver(EventObserver observer);
    void addObserver(EventObserver observer, FogOfWarView fogOfWar);
}
