package gdx_io;

import com.badlogic.gdx.InputProcessor;
import io.GameUI;
import io.ScreenPosition;

public class InputParser implements InputProcessor {
    private final GameUI gameUI;
    private final PositionTranslator translator;

    private boolean mouse_moved = false;
    private ScreenPosition lastMousePosition;
    public InputParser(GameUI gameUI, PositionTranslator translator) {
        this.gameUI = gameUI;
        this.translator = translator;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mouse_moved = false;
        lastMousePosition = translator.translate(screenX, screenY);
        gameUI.mousePress(lastMousePosition);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(!mouse_moved)
            gameUI.mouseClick(translator.translate(screenX, screenY));
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mouse_moved = true;
        var newMousePosition = translator.translate(screenX, screenY);
        gameUI.mouseDragged(lastMousePosition, newMousePosition);
        lastMousePosition = newMousePosition;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        lastMousePosition = translator.translate(screenX, screenY);
        mouse_moved = true;
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
