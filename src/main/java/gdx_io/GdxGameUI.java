package gdx_io;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import io.model.ScreenPosition;
import io.model.View;
import io.model.engine.Canvas;
import io.model.textures.TextureDrawData;

public class GdxGameUI implements ApplicationListener, Canvas {
    private final View view;
    private InputParser inputParser;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private TextureBank textureBank;

    public GdxGameUI(View view) {
        this.view = view;
    }

    @Override
    public void create() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        textureBank = new TextureBank();
        inputParser = new InputParser((x, y) -> {
            Vector3 pos = camera.unproject(new Vector3(x, y, 0));
            return new ScreenPosition(pos.x, pos.y);
        });
        Gdx.input.setInputProcessor(inputParser);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 1, (float) height / width);
        camera.update();
    }

    @Override
    public void render() {
        view.update(inputParser.getInput(), textureBank);
        ScreenUtils.clear(Color.TAN);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        view.draw(this);
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

    @Override
    public void draw(TextureDrawData texture) {
        var tex = textureBank.getTexture(texture.texture());
        batch.draw(
                tex,
                texture.position().x(),
                texture.position().y(),
                texture.height() * tex.getRegionWidth() / tex.getRegionHeight(),
                texture.height()
        );
    }

    @Override
    public float getAspectRatio() {
        return (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
    }
}
