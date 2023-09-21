package mudgame.server.actions;

import core.model.PlayerID;
import lombok.extern.slf4j.Slf4j;
import mudgame.controls.actions.Action;
import mudgame.server.state.ServerState;
import org.apache.commons.lang3.SerializationUtils;
import org.lwjgl.system.CallbackI.P;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
public final class ActionRecorder {
    public record ActionWithActor(Action action, PlayerID actor) implements Serializable { }
    public record ActionsAndState(List<ActionWithActor> actions, ServerState state) implements Serializable { }

    private final ActionsAndState data;

    public ActionRecorder(ServerState state) {
        data = new ActionsAndState(new ArrayList<>(), SerializationUtils.clone(state));
    }

    public void record(Action action, PlayerID actor) {
        data.actions.add(new ActionWithActor(action, actor));

        try (FileWriter out = new FileWriter("scenario.log")) {
            out.write(toBase64());
        } catch (IOException exception) {
            log.info("Exception while writing to file: ", exception);
        }
    }

    public String toBase64() {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(SerializationUtils.serialize(data));
    }

    public static ActionsAndState fromBase64(String base64) {
        Base64.Decoder decoder = Base64.getDecoder();
        return SerializationUtils.deserialize(decoder.decode(base64));
    }
}
