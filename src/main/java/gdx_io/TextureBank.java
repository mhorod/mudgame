package gdx_io;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.model.ScreenPosition;
import io.model.textures.TextureDrawData;

import java.util.EnumMap;

public class TextureBank implements io.model.engine.TextureBank {
    private final EnumMap<io.model.textures.Texture, TextureRegion> textures
            = new EnumMap<>(io.model.textures.Texture.class);
    private final Pixmap unitPixmap;
    private final Texture units;

    private static TextureRegion cutOut(
            Texture tex, int x, int y, float tile_width, float tile_height
    ) {
        return new TextureRegion(tex, x * (int) tile_width, y * (int) tile_height,
                (int) tile_width, (int) tile_height);
    }

    public TextureBank() {
        Texture tiles = new Texture("tiles.png");
        units = new Texture("unit.png");
        Texture arrows = new Texture("arrows.png");
        Texture ui = new Texture("ui.png");
        units.getTextureData().prepare();
        unitPixmap = units.getTextureData().consumePixmap();
        float width = 256f;
        float height = 148f;
        textures.put(io.model.textures.Texture.BUTTON_SMALL, new TextureRegion(ui, 0, 0, 450, 181));
        textures.put(io.model.textures.Texture.BUTTON_SMALL_PRESSED, new TextureRegion(ui, 0, 181, 450, 181));
        textures.put(io.model.textures.Texture.BUTTON_BIG, new TextureRegion(ui, 512, 0, 720, 181));
        textures.put(io.model.textures.Texture.BUTTON_BIG_PRESSED, new TextureRegion(ui, 512, 181, 720, 181));
        textures.put(io.model.textures.Texture.BUTTON_TINY, new TextureRegion(ui, 512, 362, 182, 181));
        textures.put(io.model.textures.Texture.BUTTON_TINY_PRESSED, new TextureRegion(ui, 512, 543, 182, 181));
        textures.put(io.model.textures.Texture.SCROLL_TOP, new TextureRegion(ui, 450, 0, 62, 34));
        textures.put(io.model.textures.Texture.SCROLL_MID, new TextureRegion(ui, 450, 34, 62, 33));
        textures.put(io.model.textures.Texture.SCROLL_BOT, new TextureRegion(ui, 450, 328, 62, 33));
        textures.put(io.model.textures.Texture.SCROLL_BACK_TOP, new TextureRegion(ui, 450, 362, 62, 33));
        textures.put(io.model.textures.Texture.SCROLL_BACK_MID, new TextureRegion(ui, 450, 395, 62, 33));
        textures.put(io.model.textures.Texture.SCROLL_BACK_BOT, new TextureRegion(ui, 450, 689, 62, 34));
        textures.put(io.model.textures.Texture.LOGO, new TextureRegion(ui, 0, 362, 449, 544));
        textures.put(io.model.textures.Texture.FOG, new TextureRegion(tiles, 5, 236, 326, 212));
        textures.put(io.model.textures.Texture.FOG_LEFT, new TextureRegion(tiles, 9, 498, 329, 282));
        textures.put(io.model.textures.Texture.FOG_RIGHT, new TextureRegion(tiles, 381, 530, 325, 282));
        textures.put(io.model.textures.Texture.FOG_TALL, new TextureRegion(tiles, 384, 227, 328, 283));
        textures.put(io.model.textures.Texture.TILE_DARK, new TextureRegion(tiles, 0, 0, 256, 222));
        textures.put(io.model.textures.Texture.TILE_LIGHT, new TextureRegion(tiles, 256, 0, 256, 222));
        textures.put(io.model.textures.Texture.TILE_HIGHLIGHT, new TextureRegion(tiles, 512, 0, 256, 148));
        textures.put(io.model.textures.Texture.PAWN, new TextureRegion(units, 88, 0, 80, 152));
        textures.put(io.model.textures.Texture.SHADOW, new TextureRegion(units, 0, 200, 256, 149));
        textures.put(io.model.textures.Texture.BASE, new TextureRegion(units, 24, 377, 208, 255));
        textures.put(io.model.textures.Texture.WARRIOR, new TextureRegion(units, 331, 22, 106, 137));
        textures.put(io.model.textures.Texture.TOWER, new TextureRegion(units, 305, 229, 158, 241));
        textures.put(io.model.textures.Texture.ARROW_NONE, cutOut(arrows, 0, 0, width, height));
        textures.put(io.model.textures.Texture.ARROW_SE_NW, cutOut(arrows, 1, 0, width, height));
        textures.put(io.model.textures.Texture.ARROW_SW_NE, cutOut(arrows, 2, 0, width, height));
        textures.put(io.model.textures.Texture.ARROW_START_NE, cutOut(arrows, 0, 1, width, height));
        textures.put(io.model.textures.Texture.ARROW_START_SE, cutOut(arrows, 1, 1, width, height));
        textures.put(io.model.textures.Texture.ARROW_START_NW, cutOut(arrows, 2, 1, width, height));
        textures.put(io.model.textures.Texture.ARROW_START_SW, cutOut(arrows, 3, 1, width, height));
        textures.put(io.model.textures.Texture.ARROW_END_NE, cutOut(arrows, 0, 2, width, height));
        textures.put(io.model.textures.Texture.ARROW_END_SE, cutOut(arrows, 1, 2, width, height));
        textures.put(io.model.textures.Texture.ARROW_END_NW, cutOut(arrows, 2, 2, width, height));
        textures.put(io.model.textures.Texture.ARROW_END_SW, cutOut(arrows, 3, 2, width, height));
        textures.put(io.model.textures.Texture.ARROW_SE_NE, cutOut(arrows, 0, 3, width, height));
        textures.put(io.model.textures.Texture.ARROW_SW_SE, cutOut(arrows, 1, 3, width, height));
        textures.put(io.model.textures.Texture.ARROW_SW_NW, cutOut(arrows, 2, 3, width, height));
        textures.put(io.model.textures.Texture.ARROW_NW_NE, cutOut(arrows, 3, 3, width, height));
    }

    public TextureRegion getTexture(io.model.textures.Texture texture) {
        return textures.get(texture);
    }

    @Override
    public boolean contains(TextureDrawData texture, ScreenPosition pos) {
        var tex = getTexture(texture.texture());
        if (tex.getTexture() != units) return false;
        int x = (int) ((pos.x() - texture.position().x()) / texture.height() *
                tex.getRegionHeight());
        int y = (int) ((pos.y() - texture.position().y()) / texture.height() *
                tex.getRegionHeight());
        if (x < 0 || x >= tex.getRegionWidth() || y < 0 || y >= tex.getRegionHeight())
            return false;
        return (unitPixmap.getPixel(tex.getRegionX() + x,
                tex.getRegionY() + tex.getRegionHeight() - y) & 0xff) != 0;
    }
}
