package gdx_io;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.model.ScreenPosition;
import io.model.textures.TextureDrawData;

public class TextureBank implements io.model.engine.TextureBank {
    public final TextureRegion tileDark, tileLight, fog, unit, shadow;
    private final TextureRegion ARROW_NONE, ARROW_SW_NE, ARROW_SE_NW,
            ARROW_START_NE, ARROW_START_SE, ARROW_START_NW, ARROW_START_SW,
            ARROW_END_NE, ARROW_END_SE, ARROW_END_NW, ARROW_END_SW,
            ARROW_SE_NE, ARROW_SW_SE, ARROW_SW_NW, ARROW_NW_NE;
    private final Pixmap unitPixmap;

    private static TextureRegion cutOut(
            Texture tex, int x, int y, float tile_width, float tile_height
    ) {
        return new TextureRegion(tex, (int) (x * tile_width), (int) (y * tile_height),
                                 (int) tile_width, (int) tile_height);
    }

    public TextureBank() {
        Texture tiles = new Texture("tiles.png");
        Texture units = new Texture("unit.png");
        Texture arrows = new Texture("arrows.png");
        units.getTextureData().prepare();
        unitPixmap = units.getTextureData().consumePixmap();
        fog = new TextureRegion(new Texture("fog.png"));
        tileDark = new TextureRegion(tiles, 0, 0, 128, 89);
        tileLight = new TextureRegion(tiles, 128, 0, 128, 89);
        unit = new TextureRegion(units, 0, 0, 128, 100);
        shadow = new TextureRegion(units, 0, 100, 128, 74);
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
            case FOG -> fog;
            case UNIT -> unit;
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
        };
    }

    @Override
    public boolean contains(TextureDrawData texture, ScreenPosition pos) {
        if (texture.texture() != io.model.textures.Texture.UNIT)
            return false;
        int x = (int) ((pos.x() - texture.position().x()) / texture.height() *
                       unit.getRegionHeight());
        int y = (int) ((pos.y() - texture.position().y()) / texture.height() *
                       unit.getRegionHeight());
        if (x < 0 || x >= unit.getRegionWidth() || y < 0 || y >= unit.getRegionHeight())
            return false;
        return (unitPixmap.getPixel(unit.getRegionX() + x,
                                    unit.getRegionY() + unit.getRegionHeight() - y) & 0xff) != 0;
    }
}
