package mudgame.server.actions;

import core.entities.model.EntityType;
import core.model.PlayerID;
import core.model.Position;
import mudgame.controls.actions.CreateEntity;
import mudgame.server.actions.ActionRecorder.ActionWithActor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ActionRecorderTest {
    @Test
    void deserialization() {
        String base64 = "rO0ABXNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAACdwQAAAACc3IANW11ZGdhbWUuc2VydmVyLmFjdGlvbnMuQWN0aW9uUmVjb3JkZXIkQWN0aW9uV2l0aEFjdG9yAAAAAAAAAAACAAJMAAZhY3Rpb250ACFMbXVkZ2FtZS9jb250cm9scy9hY3Rpb25zL0FjdGlvbjtMAAVhY3RvcnQAFUxjb3JlL21vZGVsL1BsYXllcklEO3hwc3IAJW11ZGdhbWUuY29udHJvbHMuYWN0aW9ucy5DcmVhdGVFbnRpdHkAAAAAAAAAAAIAA0wABW93bmVycQB+AARMAAhwb3NpdGlvbnQAFUxjb3JlL21vZGVsL1Bvc2l0aW9uO0wABHR5cGV0ACBMY29yZS9lbnRpdGllcy9tb2RlbC9FbnRpdHlUeXBlO3hwc3IAE2NvcmUubW9kZWwuUGxheWVySUQAAAAAAAAAAAIAAUoAAmlkeHAAAAAAAAAAAHNyABNjb3JlLm1vZGVsLlBvc2l0aW9uAAAAAAAAAAACAAJJAAF4SQABeXhwAAAADwAAAAZ+cgAeY29yZS5lbnRpdGllcy5tb2RlbC5FbnRpdHlUeXBlAAAAAAAAAAASAAB4cgAOamF2YS5sYW5nLkVudW0AAAAAAAAAABIAAHhwdAAMTUFSU0hfV0lHR0xFcQB+AAtzcQB+AAJzcQB+AAZxAH4AC3NxAH4ADAAAABIAAAAFcQB+ABBxAH4AC3g=";
        List<ActionWithActor> actions = ActionRecorder.fromBase64(base64);

        assertThat(actions).containsExactly(
                new ActionWithActor(
                        new CreateEntity(
                                EntityType.MARSH_WIGGLE,
                                new PlayerID(0),
                                new Position(15, 6)
                        ),
                        new PlayerID(0)
                ),
                new ActionWithActor(
                        new CreateEntity(
                                EntityType.MARSH_WIGGLE,
                                new PlayerID(0),
                                new Position(18, 5)
                        ),
                        new PlayerID(0)
                )
        );
    }
}
