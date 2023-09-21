package mudgame.server.actions;

import core.model.PlayerID;
import lombok.extern.slf4j.Slf4j;
import mudgame.controls.actions.Action;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
public final class ActionRecorder {
    private final List<ActionWithActor> actions = new ArrayList<>();

    public record ActionWithActor(Action action, PlayerID actor) implements Serializable { }

    public void record(Action action, PlayerID actor) {
        actions.add(new ActionWithActor(action, actor));
        log.debug(toBase64());
    }

    public String toBase64() {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(SerializationUtils.serialize((Serializable) actions));
    }

    public static List<ActionWithActor> fromBase64(String base64) {
        Base64.Decoder decoder = Base64.getDecoder();
        return SerializationUtils.deserialize(decoder.decode(base64));
    }
}
