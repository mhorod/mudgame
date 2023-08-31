package core.entities.events;

import core.EntityEvents;
import core.SerializationTestBase;
import core.entities.components.Vision;
import core.entities.model.Components;
import core.entities.model.Entity;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import org.junit.jupiter.api.Test;

class EventSerializationTest extends SerializationTestBase {

    @Test
    void create_entity_is_serializable() {
        Components components = Components.of(new Vision(1));
        CreateEntity e = EntityEvents.create(components, 0, new Position(0, 0));
        assertCanSerialize(e);
    }

    @Test
    void move_entity_is_serializable() {
        MoveEntity e = EntityEvents.move(new EntityID(0), new Position(0, 0));
        assertCanSerialize(e);
    }

    @Test
    void place_entity_is_serializable() {
        Entity entity = new Entity(
                Components.of(),
                new EntityID(0),
                new PlayerID(0)
        );
        PlaceEntity e = EntityEvents.place(entity, new Position(0, 0));
        assertCanSerialize(e);
    }

    @Test
    void remove_entity_is_serializable() {
        RemoveEntity e = EntityEvents.remove(new EntityID(0));
        assertCanSerialize(e);
    }

}
