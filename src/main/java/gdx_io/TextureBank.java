package gdx_io;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.model.ScreenPosition;
import io.model.textures.TextureDrawData;

public class TextureBank implements io.model.engine.TextureBank {
    public final TextureRegion tileDark, tileLight, fog, unit, shadow;
    private final TextureRegion arrow_none, arrow_sw_ne, arrow_se_nw,
            arrow_start_ne, arrow_start_se, arrow_start_nw, arrow_start_sw,
            arrow_end_ne, arrow_end_se, arrow_end_nw, arrow_end_sw,
            arrow_se_ne, arrow_sw_se, arrow_sw_nw, arrow_nw_ne;
    private final Pixmap unitPixmap;

    private static TextureRegion cutOut(Texture tex, int x, int y, float tile_width, float tile_height) {
        return new TextureRegion(tex, (int) (x * tile_width), (int) (y * tile_height), (int) tile_width, (int) tile_height);
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
        float tile_width = 256f;
        float tile_height = 148f;
        arrow_none = cutOut(arrows, 0, 0, tile_width, tile_height);
        arrow_se_nw = cutOut(arrows, 1, 0, tile_width, tile_height);
        arrow_sw_ne = cutOut(arrows, 2, 0, tile_width, tile_height);
        arrow_start_ne = cutOut(arrows, 0, 1, tile_width, tile_height);
        arrow_start_se = cutOut(arrows, 1, 1, tile_width, tile_height);
        arrow_start_nw = cutOut(arrows, 2, 1, tile_width, tile_height);
        arrow_start_sw = cutOut(arrows, 3, 1, tile_width, tile_height);
        arrow_end_ne = cutOut(arrows, 0, 2, tile_width, tile_height);
        arrow_end_se = cutOut(arrows, 1, 2, tile_width, tile_height);
        arrow_end_nw = cutOut(arrows, 2, 2, tile_width, tile_height);
        arrow_end_sw = cutOut(arrows, 3, 2, tile_width, tile_height);
        arrow_se_ne = cutOut(arrows, 0, 3, tile_width, tile_height);
        arrow_sw_se = cutOut(arrows, 1, 3, tile_width, tile_height);
        arrow_sw_nw = cutOut(arrows, 2, 3, tile_width, tile_height);
        arrow_nw_ne = cutOut(arrows, 3, 3, tile_width, tile_height);
    }

    public TextureRegion getTexture(io.model.textures.Texture texture) {
        return switch (texture) {
            case TILE_DARK -> tileDark;
            case TILE_LIGHT -> tileLight;
            case FOG -> fog;
            case UNIT -> unit;
            case SHADOW -> shadow;
            case ARROW_NONE -> arrow_none;
            case ARROW_SW_NE -> arrow_sw_ne;
            case ARROW_SE_NW -> arrow_se_nw;
            case ARROW_START_NE -> arrow_start_ne;
            case ARROW_START_SE -> arrow_start_se;
            case ARROW_START_NW -> arrow_start_nw;
            case ARROW_START_SW -> arrow_start_sw;
            case ARROW_END_NE -> arrow_end_ne;
            case ARROW_END_SE -> arrow_end_se;
            case ARROW_END_NW -> arrow_end_nw;
            case ARROW_END_SW -> arrow_end_sw;
            case ARROW_SE_NE -> arrow_se_ne;
            case ARROW_SW_SE -> arrow_sw_se;
            case ARROW_SW_NW -> arrow_sw_nw;
            case ARROW_NW_NE -> arrow_nw_ne;
        };
    }

    @Override
    public boolean contains(TextureDrawData texture, ScreenPosition pos) {
        if (texture.texture() != io.model.textures.Texture.UNIT) return false;
        int x = (int) ((pos.x() - texture.position().x()) / texture.height() * unit.getRegionHeight());
        int y = (int) ((pos.y() - texture.position().y()) / texture.height() * unit.getRegionHeight());
        if (x < 0 || x >= unit.getRegionWidth() || y < 0 || y >= unit.getRegionHeight()) return false;
        return (unitPixmap.getPixel(unit.getRegionX() + x, unit.getRegionY() + unit.getRegionHeight() - y) & 0xff) != 0;
    }
}
