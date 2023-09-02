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
    ArrayList<Event> events = new ArrayList<>();
    int mouseX, mouseY;
    int distance = 0;

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
        distance = 0;
        mouseX = screenX;
        mouseY = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (distance < 10)
            events.add(new Click(translator.translate(screenX, screenY)));
        mouseX = screenX;
        mouseY = screenY;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        distance += Math.abs(screenX - mouseX) + Math.abs(screenY - mouseY);
        mouseX = screenX;
        mouseY = screenY;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouseX = screenX;
        mouseY = screenY;
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        events.add(new Scroll(amountY));
        return true;
    }
}
