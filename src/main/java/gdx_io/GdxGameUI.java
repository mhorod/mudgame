package gdx_io;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import io.model.ScreenPosition;
import io.model.View;
import io.model.engine.Canvas;
import io.model.textures.TextureDrawData;
import middleware.remote_clients.RemoteNetworkClient;

public class GdxGameUI implements ApplicationListener, Canvas {
    private final View view;
    private InputParser inputParser;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private TextureBank textureBank;
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    public GdxGameUI(View view) {
        this.view = view;
    }

    @Override
    public void create() {
        font = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"),
                false);
        glyphLayout = new GlyphLayout();
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
        RemoteNetworkClient.GLOBAL_CLIENT.processAllMessages();
        view.update(inputParser.getInput(), textureBank, this, new AWTStateManager());
        ScreenUtils.clear(Color.WHITE);
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
    public void drawColored(TextureDrawData texture, float alpha, io.model.engine.Color color) {
        var c = switch (color) {
            case WHITE -> Color.WHITE;
            case PINK -> new Color(0xff8ce1ff);
            case GREEN -> Color.GREEN;
            case BLUE -> Color.BLUE;
            case RED -> Color.RED;
            case CYAN -> Color.CYAN;
            case MAGENTA -> Color.MAGENTA;
            case YELLOW -> Color.YELLOW;
            case ORANGE -> Color.ORANGE;
            case PURPLE -> Color.PURPLE;
        };
        batch.setColor(new Color(c.r, c.g, c.b, alpha));
        var tex = textureBank.getTexture(texture.texture());
        batch.draw(
                tex,
                texture.position().x(),
                texture.position().y(),
                texture.height() * tex.getRegionWidth() / tex.getRegionHeight(),
                texture.height()
        );
        batch.setColor(1, 1, 1, 1);

    }

    @Override
    public void drawText(String text, ScreenPosition position, float height) {
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()));
        var fontHeight = height * Gdx.graphics.getWidth();
        font.getData().setScale(fontHeight * 0.0208f);
        font.setColor(Color.BLACK);
        font.draw(batch, text, position.x() * Gdx.graphics.getWidth(),
                position.y() * Gdx.graphics.getWidth() + fontHeight);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public float getTextAspectRatio(String text) {
        font.getData().setScale(1);
        glyphLayout.setText(font, text);
        return (glyphLayout.width) / glyphLayout.height;
    }

    @Override
    public float getAspectRatio() {
        return (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
    }
}
