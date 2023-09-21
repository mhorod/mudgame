package gdx_io;

import io.model.engine.StateManager;
import lombok.extern.slf4j.Slf4j;
import mudgame.server.state.ServerState;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Future;

@Slf4j
public class AWTStateManager implements StateManager {
    @Override
    public Future<Optional<ServerState>> loadState() {
        FutureTask<Optional<ServerState>> task = new FutureTask<>(this::loadStateBlocking);
        new Thread(task).start();
        return task;
    }

    @Override
    public void saveState(ServerState state) {
        new Thread(() -> saveStateBlocking(state)).start();
    }

    private Optional<ServerState> loadStateBlocking() {
        FileDialog dialog = new FileDialog((Frame) null, "Load state", FileDialog.LOAD);
        dialog.setFilenameFilter((dir, name) -> name.endsWith(".mud"));
        dialog.setVisible(true);

        File[] files = dialog.getFiles();
        if (files.length == 0)
            return Optional.empty();

        try {
            FileInputStream fileInputStream = new FileInputStream(files[0]);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return Optional.ofNullable((ServerState) objectInputStream.readObject());
        } catch (Exception exception) {
            log.info("Exception while loading file: ", exception);
            return Optional.empty();
        }
    }

    private void saveStateBlocking(ServerState state) {
        FileDialog dialog = new FileDialog((Frame) null, "Save state", FileDialog.SAVE);
        dialog.setFilenameFilter((dir, name) -> name.endsWith(".mud"));
        dialog.setVisible(true);

        File[] files = dialog.getFiles();
        if (files.length == 0)
            return;

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(files[0]);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(state);
            objectOutputStream.close();
            log.info("Filed saved successfully");
        } catch (Exception exception) {
            log.info("Exception while saving file: ", exception);
        }
    }
}
