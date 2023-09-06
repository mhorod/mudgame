package gdx_io;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.model.ScreenPosition;
import io.model.textures.TextureDrawData;

public class TextureBank implements io.model.engine.TextureBank {
    public final TextureRegion tileDark, tileLight, fog, fogTall, fogLeft, fogRight, unit, shadow, base, warrior, tower, tileHighlight;
    private final TextureRegion ARROW_NONE, ARROW_SW_NE, ARROW_SE_NW,
            ARROW_START_NE, ARROW_START_SE, ARROW_START_NW, ARROW_START_SW,
            ARROW_END_NE, ARROW_END_SE, ARROW_END_NW, ARROW_END_SW,
            ARROW_SE_NE, ARROW_SW_SE, ARROW_SW_NW, ARROW_NW_NE;
    public final TextureRegion buttonSmall, buttonSmallPressed, logo,
            scrollTop, scrollMid, scrollBot,
            scrollBackTop, scrollBackMid, scrollBackBot;
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
        buttonSmall = new TextureRegion(ui, 0, 0, 450, 181);
        scrollTop = new TextureRegion(ui, 450, 0, 62, 34);
        scrollMid = new TextureRegion(ui, 450, 34, 62, 33);
        scrollBot = new TextureRegion(ui, 450, 328, 62, 33);
        scrollBackTop = new TextureRegion(ui, 450, 362, 62, 33);
        scrollBackMid = new TextureRegion(ui, 450, 395, 62, 33);
        scrollBackBot = new TextureRegion(ui, 450, 689, 62, 34);
        buttonSmallPressed = new TextureRegion(ui, 0, 181, 450, 181);
        logo = new TextureRegion(ui, 0, 362, 449, 544);
        fog = new TextureRegion(tiles, 5, 236, 326, 212);
        fogLeft = new TextureRegion(tiles, 9, 498, 329, 282);
        fogRight = new TextureRegion(tiles, 381, 530, 325, 282);
        fogTall = new TextureRegion(tiles, 384, 227, 328, 283);
        tileDark = new TextureRegion(tiles, 0, 0, 256, 222);
        tileLight = new TextureRegion(tiles, 256, 0, 256, 222);
        tileHighlight = new TextureRegion(tiles, 512, 0, 256, 148);
        unit = new TextureRegion(units, 88, 0, 80, 152);
        shadow = new TextureRegion(units, 0, 200, 256, 149);
        base = new TextureRegion(units, 24, 377, 208, 255);
        warrior = new TextureRegion(units, 331, 22, 106, 137);
        tower = new TextureRegion(units, 305, 229, 158, 241);
        float width = 256f;
        float height = 148f;
        ARROW_NONE = cutOut(arrows, 0, 0, width, height);
        ARROW_SE_NW = cutOut(arrows, 1, 0, width, height);
        ARROW_SW_NE = cutOut(arrows, 2, 0, width, height);
        ARROW_START_NE = cutOut(arrows, 0, 1, width, height);
        ARROW_START_SE = cutOut(arrows, 1, 1, width, height);
        ARROW_START_NW = cutOut(arrows, 2, 1, width, height);
        ARROW_START_SW = cutOut(arrows, 3, 1, width, height);
        ARROW_END_NE = cutOut(arrows, 0, 2, width, height);
        ARROW_END_SE = cutOut(arrows, 1, 2, width, height);
        ARROW_END_NW = cutOut(arrows, 2, 2, width, height);
        ARROW_END_SW = cutOut(arrows, 3, 2, width, height);
        ARROW_SE_NE = cutOut(arrows, 0, 3, width, height);
        ARROW_SW_SE = cutOut(arrows, 1, 3, width, height);
        ARROW_SW_NW = cutOut(arrows, 2, 3, width, height);
        ARROW_NW_NE = cutOut(arrows, 3, 3, width, height);
    }

    public TextureRegion getTexture(io.model.textures.Texture texture) {
        return switch (texture) {
            case TILE_DARK -> tileDark;
            case TILE_LIGHT -> tileLight;
            case TILE_HIGHLIGHT -> tileHighlight;
            case FOG -> fog;
            case FOG_TALL -> fogTall;
            case FOG_LEFT -> fogLeft;
            case FOG_RIGHT -> fogRight;
            case PAWN -> unit;
            case SHADOW -> shadow;
            case ARROW_NONE -> ARROW_NONE;
            case ARROW_SW_NE -> ARROW_SW_NE;
            case ARROW_SE_NW -> ARROW_SE_NW;
            case ARROW_START_NE -> ARROW_START_NE;
            case ARROW_START_SE -> ARROW_START_SE;
            case ARROW_START_NW -> ARROW_START_NW;
            case ARROW_START_SW -> ARROW_START_SW;
            case ARROW_END_NE -> ARROW_END_NE;
            case ARROW_END_SE -> ARROW_END_SE;
            case ARROW_END_NW -> ARROW_END_NW;
            case ARROW_END_SW -> ARROW_END_SW;
            case ARROW_SE_NE -> ARROW_SE_NE;
            case ARROW_SW_SE -> ARROW_SW_SE;
            case ARROW_SW_NW -> ARROW_SW_NW;
            case ARROW_NW_NE -> ARROW_NW_NE;
            case WARRIOR -> warrior;
            case BASE -> base;
            case BUTTON_SMALL -> buttonSmall;
            case BUTTON_SMALL_PRESSED -> buttonSmallPressed;
            case SCROLL_TOP -> scrollTop;
            case SCROLL_MID -> scrollMid;
            case SCROLL_BOT -> scrollBot;
            case SCROLL_BACK_TOP -> scrollBackTop;
            case SCROLL_BACK_MID -> scrollBackMid;
            case SCROLL_BACK_BOT -> scrollBackBot;
            case LOGO -> logo;
            case TOWER -> tower;
        };
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
