package core.fogofwar.events;

import core.SerializationTestBase;
import core.fogofwar.events.SetVisibility.SetPositionVisibility;
import core.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

class SetVisibilityTest extends SerializationTestBase {

    @Test
    void set_visibility_is_serializable() {
        assertCanSerialize(new SetVisibility(List.of()));

        SetPositionVisibility p1 = new SetPositionVisibility(new Position(0, 0), true);
        SetPositionVisibility p2 = new SetPositionVisibility(new Position(1, 0), false);
        assertCanSerialize(SetVisibility.of(p1, p2));
    }
}