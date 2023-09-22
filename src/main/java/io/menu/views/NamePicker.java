package io.menu.views;

import io.menu.Rectangle;
import io.menu.UIComponent;
import io.menu.buttons.Button;
import io.menu.buttons.ButtonMedium;
import io.menu.components.ButtonBlock;
import io.menu.components.Label;
import io.model.engine.Canvas;
import io.model.engine.StateManager;
import io.model.engine.TextManager;
import io.model.engine.TextureBank;
import io.model.input.Input;
import io.model.input.events.Click;
import io.model.input.events.EventHandler;
import io.model.input.events.Scroll;
import io.views.SimpleView;
import lombok.extern.slf4j.Slf4j;
import middleware.clients.ServerClient;

import java.util.List;
import java.util.Optional;

@Slf4j
public class NamePicker extends SimpleView implements EventHandler {
    private final ButtonBlock buttonsLeft;
    private final ButtonBlock buttonsRight;
    private String adjective, noun;

    private Label name = new Label("********");
    private final Button ok = new ButtonMedium(new Label("OK"));
    private static final List<String> adjectives = List.of("Mud", "Marsh", "Bog", "Puddle");
    private static final List<String> nouns = List.of("Man", "Boy", "Woman", "Girl");

    private final ServerClient client;

    public NamePicker(ServerClient client) {
        this.client = client;
        buttonsLeft = new ButtonBlock(
                0.1f,
                adjectives.stream().map(adj -> (UIComponent) new Label(adj)).toList(),
                adjectives.stream().map(adj -> (Runnable) () -> adjective = adj).toList()
        );
        buttonsRight = new ButtonBlock(
                0.1f,
                nouns.stream().map(noun -> (UIComponent) new Label(noun)).toList(),
                nouns.stream().map(noun -> (Runnable) () -> this.noun = noun).toList()
        );
    }

    @Override
    public void draw(Canvas canvas) {
        buttonsLeft.draw(canvas);
        buttonsRight.draw(canvas);
        name.draw(canvas);
        ok.draw(canvas);
    }

    @Override
    public void update(Input input, TextureBank bank, TextManager mgr, StateManager stateManager) {
        var window = new Rectangle(0.025f, 0.025f, 0.95f, input.window().height() / input.window().width() - 0.05f);

        buttonsLeft.fitInto(new Rectangle(
                window.position.x(),
                window.position.y() + window.height * 0.3f,
                window.width() * 0.5f,
                window.height * 0.7f
        ), mgr);

        buttonsRight.fitInto(new Rectangle(
                window.position.x() + window.width() * 0.5f,
                window.position.y() + window.height * 0.3f,
                window.width() * 0.5f,
                window.height * 0.7f
        ), mgr);

        name = new Label(getName());
        name.fitInto(new Rectangle(
                window.position.x(),
                window.position.y() + window.height * 0.05f,
                window.width() * 0.5f,
                window.height * 0.2f
        ), mgr);

        ok.fitInto(new Rectangle(
                window.position.x() + window.width() * 0.5f,
                window.position.y() + window.height * 0.05f,
                window.width() * 0.5f,
                window.height * 0.2f
        ), mgr);


        input.events().forEach(event -> event.accept(this));
        buttonsLeft.update(input.mouse().position(), input.mouse().leftPressed());
        buttonsRight.update(input.mouse().position(), input.mouse().leftPressed());
        ok.update(input.mouse().position(), input.mouse().leftPressed());
    }

    private String getName() {
        String s1 = Optional.ofNullable(adjective).orElse("****");
        String s2 = Optional.ofNullable(noun).orElse("****");
        return s1 + s2;
    }

    @Override
    public void onClick(Click click) {
        buttonsLeft.click(click.position());
        buttonsRight.click(click.position());
        if (ok.contains(click.position())) {
            client.setName(getName());
            changeView(new RoomSelect(client));
        }
    }

    @Override
    public void onScroll(Scroll scroll) {

    }
}
