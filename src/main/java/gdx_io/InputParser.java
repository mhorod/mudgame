package gdx_io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import io.model.input.Input;
import io.model.input.MouseInfo;
import io.model.input.WindowInfo;
import io.model.input.events.Click;
import io.model.input.events.Event;
import io.model.input.events.Scroll;

import java.util.ArrayList;

public class InputParser implements InputProcessor {
    private final PositionTranslator translator;

    private boolean mouse_moved = false;
    ArrayList<Event> events = new ArrayList<>();

    public InputParser(PositionTranslator translator) {
        this.translator = translator;
    }

    public Input getInput() {
        var collectedEvents = events;
        events = new ArrayList<>();
        return new Input(
                collectedEvents,
                new MouseInfo(
                        translator.translate(
                                Gdx.input.getX(0),
                                Gdx.input.getY(0)
                        ),
                        Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT),
                        Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.MIDDLE),
                        Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.RIGHT)
                ),
                new WindowInfo(
                        Gdx.graphics.getWidth(),
                        Gdx.graphics.getHeight()
                ),
                1.f / 60
        );
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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!mouse_moved)
            events.add(new Click(translator.translate(screenX, screenY)));
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mouse_moved = true;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouse_moved = true;
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        events.add(new Scroll(amountY));
        return true;
    }
}
