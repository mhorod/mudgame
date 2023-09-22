package io.game.ui;

import core.entities.model.Entity;
import core.entities.model.EntityType;
import core.resources.PlayerResourcesView;
import core.resources.ResourceType;
import core.turns.PlayerTurnView;
import io.menu.Rectangle;
import io.menu.UIComponent;
import io.menu.buttons.Button;
import io.menu.buttons.ButtonSmall;
import io.menu.components.Image;
import io.menu.components.Label;
import io.menu.containers.HBox;
import io.model.ScreenPosition;
import io.model.engine.Canvas;
import io.model.engine.Color;
import io.model.engine.TextManager;
import io.model.textures.Texture;

import java.util.List;

public class HUD {
    Button pawn = new ButtonSmall(new Image(Texture.PAWN));
    Button warrior = new ButtonSmall(new Image(Texture.WARRIOR));
    Button marshWiggle = new ButtonSmall(new Image(Texture.MARSH_WIGGLE));
    Button base = new ButtonSmall(new Image(Texture.BASE));
    Button tower = new ButtonSmall(new Image(Texture.TOWER));


    Button nextTurn = new ButtonSmall(new Image(Texture.BASE, Color.BLUE));
    Button quit = new ButtonSmall(new Label("QUIT"));
    Button save = new ButtonSmall(new Label("SAVE"));
    boolean nextTurnEnabled = true;

    List<Button> buttons = List.of(pawn, warrior, marshWiggle, base, tower);

    UIComponent content = new HBox(0.1f, buttons);

    ResourceInfo resourceInfo;
    EntityInfo entityInfo;

    private final PlayerTurnView turnView;
    private final PlayerResourcesView resourcesView;

    public HUD(PlayerTurnView turnView, PlayerResourcesView resourcesView) {
        this.turnView = turnView;
        this.resourcesView = resourcesView;
        resourceInfo = new ResourceInfo(resourcesView.amount(ResourceType.MUD));
    }


    public void update(ScreenPosition mouse, float aspectRatio, boolean pressed, TextManager mgr) {
        resourceInfo.setMudCount(resourcesView.amount(ResourceType.MUD));

        content.fitInto(new Rectangle(0, 0.01f, 1, 0.1f), mgr);
        buttons.forEach(button -> button.update(mouse, pressed));

        nextTurn = new ButtonSmall(new Image(Texture.BASE, Color.fromPlayerId(turnView.currentPlayer())));
        nextTurn.setPressed(!nextTurnEnabled || !turnView.isMyTurn());
        nextTurn.fitInto(new Rectangle(0.84f, 0.01f, 0.15f, 0.15f), mgr);
        quit.fitInto(new Rectangle(0.01f, 0.01f, 0.15f, 0.15f), mgr);
        save.fitInto(new Rectangle(0.01f, 0.17f, 0.15f, 0.15f), mgr);
        nextTurn.update(mouse, pressed);
        quit.update(mouse, pressed);
        save.update(mouse, pressed);
        resourceInfo.fitInto(new Rectangle(0, aspectRatio - 0.15f, 0.1f / resourceInfo.getAspectRatio(mgr), 0.1f), mgr);
        if (entityInfo != null)
            entityInfo.fitInto(new Rectangle(0, aspectRatio - 0.15f, 1, 0.15f), mgr);
    }

    public void draw(Canvas canvas) {
        content.draw(canvas);
        nextTurn.draw(canvas);
        quit.draw(canvas);
        save.draw(canvas);
        resourceInfo.draw(canvas);
        if (entityInfo != null)
            entityInfo.draw(canvas);
    }

    public void showEntityInfo(Entity entity) {
        entityInfo = new EntityInfo(entity);
    }

    public void hideEntityInfo() {
        entityInfo = null;
    }

    public void setEndTurnEnabled(boolean enabled) {
        nextTurn.setPressed(!enabled);
        nextTurnEnabled = enabled;
    }

    public boolean click(ScreenPosition position, HUDListener listener, HUDMetaListener metaListener) {
        if (pawn.contains(position))
            listener.onEntityTypeSelected(EntityType.PAWN);
        else if (warrior.contains(position))
            listener.onEntityTypeSelected(EntityType.WARRIOR);
        else if (marshWiggle.contains(position))
            listener.onEntityTypeSelected(EntityType.MARSH_WIGGLE);
        else if (base.contains(position))
            listener.onEntityTypeSelected(EntityType.BASE);
        else if (tower.contains(position))
            listener.onEntityTypeSelected(EntityType.TOWER);
        else if (nextTurn.contains(position))
            listener.onEndTurn();
        else if (quit.contains(position))
            metaListener.onQuit();
        else if (save.contains(position))
            metaListener.onSave();
        else return false;
        return true;
    }

    public void setPressed(EntityType type) {
        switch (type) {
            case BASE -> base.setPressed(true);
            case PAWN -> pawn.setPressed(true);
            case TOWER -> tower.setPressed(true);
            case WARRIOR -> warrior.setPressed(true);
            case MARSH_WIGGLE -> marshWiggle.setPressed(true);
        }
    }

    public void clear() {
        for (var button : buttons)
            button.setPressed(false);
    }

}
