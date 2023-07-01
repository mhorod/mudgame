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
import io.Drawer;
import io.GameUI;
import io.ScreenPosition;
import io.Tile;
import org.lwjgl.system.CallbackI;

import java.util.List;

public class GdxGameUI implements ApplicationListener, Drawer {

    private final GameUI gameUI;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private TextureRegion tile_dark;
    private TextureRegion tile_light;
    public GdxGameUI(GameUI gameUI) {
        this.gameUI = gameUI;

    }

    @Override
    public void create() {
        Texture tiles = new Texture("tiles.png");
        tile_dark = new TextureRegion(tiles, 0, 0, 128, 89);
        tile_light = new TextureRegion(tiles, 128, 0, 128, 89);
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

    @Override
    public void drawTiles(List<Tile> tiles, float tile_width) {
        tiles.forEach(tile -> drawTile(tile, tile_width));
    }

    @Override
    public float getAspectRatio() {
        System.out.println(Gdx.graphics.getWidth() +" " + Gdx.graphics.getHeight());
        return (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
    }
}
