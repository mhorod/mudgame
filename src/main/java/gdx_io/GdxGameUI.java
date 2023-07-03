package gdx_io;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import io.*;
import org.lwjgl.system.CallbackI;

import java.util.List;

public class GdxGameUI implements ApplicationListener, Drawer {

    private final GameUI gameUI;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private TextureRegion tile_dark;
    private TextureRegion tile_light;

    private TextureRegion arrow_none, arrow_sw_ne, arrow_se_nw,
        arrow_start_ne, arrow_start_se, arrow_start_nw, arrow_start_sw,
        arrow_end_ne, arrow_end_se, arrow_end_nw, arrow_end_sw,
        arrow_se_ne, arrow_sw_se, arrow_sw_nw, arrow_nw_ne;
    public GdxGameUI(GameUI gameUI) {
        this.gameUI = gameUI;

    }
    private static final int TILE_WIDTH = 128;
    private static final int TILE_HEIGHT = 89;

    private TextureRegion cutOut(Texture tex, int x, int y, float tile_width, float tile_height) {
        return new TextureRegion(tex, (int) (x * tile_width), (int) (y * tile_height), (int) tile_width, (int) tile_height);
    }

    @Override
    public void create() {
        Texture tiles = new Texture("tiles.png");
        tile_dark = new TextureRegion(tiles, 0, 0, TILE_WIDTH, TILE_HEIGHT);
        tile_light = new TextureRegion(tiles, TILE_WIDTH, 0, TILE_WIDTH, TILE_HEIGHT);
        Texture arrows = new Texture("arrows.png");
        float tile_width = 128f;
        float tile_height = 74f;
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
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        Gdx.input.setInputProcessor(new InputParser(gameUI, (x, y) -> {
            Vector3 pos = camera.unproject(new Vector3(x, y, 0));
            return new ScreenPosition(pos.x, pos.y);
        }));
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 1, (float) height / width);
        camera.update();
        gameUI.resize(width, height);
    }

    @Override
    public void render() {
        gameUI.update();

        ScreenUtils.clear(Color.TAN);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        gameUI.draw(this);
        batch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    private void drawTile(Tile tile, float tile_width) {
        var texture = switch(tile.kind()) {
            case LIGHT -> tile_light;
            case DARK -> tile_dark;
        };
        float tile_surface_height = tile_width / Tile.ASPECT_RATIO;
        float tile_height = tile_width * texture.getRegionHeight() / texture.getRegionWidth();
        float x = tile.middle().x() - tile_width / 2;
        float y = tile.middle().y() + tile_surface_height / 2 - tile_height;
        batch.draw(texture, x, y, tile_width, tile_height);
    }

    private void drawArrow(Arrow arrow, float tile_width) {
        var texture = switch(arrow.from) {
            case NONE -> switch(arrow.to) {
                case NONE -> arrow_none;
                case SE -> arrow_start_se;
                case SW -> arrow_start_sw;
                case NE -> arrow_start_ne;
                case NW -> arrow_start_nw;
            };
            case SE -> switch(arrow.to) {
                case NONE -> arrow_end_se;
                case SE -> arrow_none;
                case SW -> arrow_sw_se;
                case NE -> arrow_se_ne;
                case NW -> arrow_se_nw;
            };
            case SW -> switch(arrow.to) {
                case NONE -> arrow_end_sw;
                case SE -> arrow_sw_se;
                case SW -> arrow_none;
                case NE -> arrow_sw_ne;
                case NW -> arrow_sw_nw;
            };
            case NE -> switch(arrow.to) {
                case NONE -> arrow_end_ne;
                case SE -> arrow_se_ne;
                case SW -> arrow_sw_ne;
                case NE -> arrow_none;
                case NW -> arrow_nw_ne;
            };
            case NW -> switch(arrow.to) {
                case NONE -> arrow_end_nw;
                case SE -> arrow_se_nw;
                case SW -> arrow_sw_nw;
                case NE -> arrow_nw_ne;
                case NW -> arrow_none;
            };
        };
        float tile_surface_height = tile_width / Tile.ASPECT_RATIO;
        float tile_height = tile_width * texture.getRegionHeight() / texture.getRegionWidth();
        float x = arrow.middle.x() - tile_width / 2;
        float y = arrow.middle.y() + tile_surface_height / 2 - tile_height;
        batch.draw(texture, x, y, tile_width, tile_height);
    }

    private void drawTop(Top top, float tileWidth) {
        if(top instanceof Arrow)
            drawArrow((Arrow) top, tileWidth);
    }
    @Override
    public void drawTiles(List<Tile> tiles, float tile_width) {
        tiles.forEach(tile -> drawTile(tile, tile_width));
    }

    @Override
    public void drawTops(List<Top> tops, float tile_width) {
        tops.forEach(top -> drawTop(top, tile_width));
    }

    @Override
    public float getAspectRatio() {
        return (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
    }
}
