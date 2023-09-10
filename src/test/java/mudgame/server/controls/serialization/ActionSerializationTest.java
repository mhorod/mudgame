package mudgame.server.controls.serialization;

import core.SerializationTestBase;
import core.model.EntityID;
import mudgame.controls.actions.AttackEntityAction;
import mudgame.controls.actions.CreateEntity;
import mudgame.controls.actions.MoveEntity;
import org.junit.jupiter.api.Test;

import static core.entities.model.EntityType.PAWN;
import static testutils.Players.PLAYER_0;
import static testutils.Positions.pos;

public class ActionSerializationTest extends SerializationTestBase {

    @Test
    void AttackEntityAction_is_serializable() {
        assertCanSerialize(new AttackEntityAction(new EntityID(0), new EntityID(1)));
    }

    @Test
    void CreateEntity_is_serializable() {
        assertCanSerialize(new CreateEntity(PAWN, PLAYER_0, pos(0, 1)));
    }

    @Test
    void MoveEntity_is_serializable() {
        assertCanSerialize(new MoveEntity(new EntityID(0), pos(2, 2)));
    }
}
