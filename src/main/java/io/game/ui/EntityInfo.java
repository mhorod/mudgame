package io.game.ui;

import core.entities.model.Entity;
import core.resources.ResourceType;
import io.game.world.WorldTexture;
import io.menu.Rectangle;
import io.menu.UIComponent;
import io.menu.components.Image;
import io.menu.components.Label;
import io.menu.containers.VBox;
import io.model.engine.Canvas;
import io.model.engine.Color;
import io.model.engine.TextManager;
import io.model.textures.Texture;

import java.util.ArrayList;

public class EntityInfo implements UIComponent {
    private final Image bannerTop = new Image(Texture.BANNER_TOP);
    private final Entity entity;
    private final Image entityImage;

    private final VBox stats;

    public EntityInfo(Entity entity) {
        this.entity = entity;
        this.entityImage = new Image(
                WorldTexture.from(entity.type()).getTexture(),
                Color.fromPlayerId(entity.owner())
        );
        ArrayList<UIComponent> labels = new ArrayList<>();
        entity.getHealth().ifPresent(health -> labels.add(new Label("HP: " + health.getCurrentHealth())));
        entity.getAttack().ifPresent(attack -> labels.add(new Label("ACK: " + attack.damage())));
        entity.getProduction().ifPresent(production -> labels.add(new Label("PROD: " + production.amount(ResourceType.MUD))));
        this.stats = new VBox(0.01f, labels);
    }

    @Override
    public float getAspectRatio(TextManager mgr) {
        return bannerTop.getAspectRatio(mgr);
    }

    @Override
    public void fitInto(Rectangle rectangle, TextManager mgr) {
        bannerTop.fitInto(rectangle, mgr);
        var rect = this.bannerTop.getBounds();
        entityImage.fitInto(new Rectangle(
                rect.position.x() + 0.01f,
                rect.position.y() + 0.01f,
                rect.width() / 3,
                rect.height - 0.02f
        ), mgr);
        stats.fitInto(new Rectangle(
                rect.position.x() + rect.width() * 0.4f,
                rect.position.y() + 0.01f,
                rect.width() * 0.5f,
                rect.height - 0.02f
        ), mgr);
    }

    @Override
    public void draw(Canvas canvas) {
        bannerTop.draw(canvas);
        entityImage.draw(canvas);
        stats.draw(canvas);
    }
}
