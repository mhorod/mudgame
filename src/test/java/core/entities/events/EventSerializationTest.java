package core.entities.events;

import core.EntityEvents;
import core.SerializationTestBase;
import core.entities.components.Component;
import core.entities.components.Vision;
import core.entities.model.Entity;
import core.entities.model.EntityData;
import core.model.EntityID;
import core.model.PlayerID;
import core.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.entities.model.EntityType.PAWN;

class EventSerializationTest extends SerializationTestBase {

    @Test
    void create_entity_is_serializable() {
        List<Component> components = List.of(new Vision(1));
        CreateEntity e = EntityEvents.create(PAWN, 0, new Position(0, 0));
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
                EntityData.ofType(PAWN),
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
