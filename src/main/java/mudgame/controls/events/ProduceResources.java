package mudgame.controls.events;

import core.event.Event;
import core.resources.Resources;

public record ProduceResources(Resources resources) implements Event {
}
