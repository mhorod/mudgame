package core.entities.events;

import core.EntityEvents;
import core.SerializationTestBase;
import core.entities.components.Component;
import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.List;

class EventSerializationTest extends SerializationTestBase {
    record MockEntityData() implements EntityData, Serializable {

        @Override
        public List<Component> components() {
            return List.of();
        }
    }

    @Test
    void create_entity_is_serializable() {
        EntityData entityData = new MockEntityData();
        CreateEntity e = EntityEvents.create(entityData, 0, new Position(0, 0));
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
                new MockEntityData(),
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
