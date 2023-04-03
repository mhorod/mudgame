import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

class SimpleGame extends Game {
        public SpriteBatch batch;
        public OrthographicCamera camera;
        public Texture cubeTexture;

        @Override
        public void create() {
            batch = new SpriteBatch();
            cubeTexture = new Texture(Gdx.files.internal("cube.png"));
            camera = new OrthographicCamera();
            camera.setToOrtho(false, 1440, 900);
            camera.update();
        }

        @Override
        public void render() {
            ScreenUtils.clear(Color.TAN);
            batch.begin();
            batch.draw(cubeTexture, 0, 0);
            batch.end();
            super.render();
        }

        @Override
        public void dispose() {
            batch.dispose();
            cubeTexture.dispose();
        }
}

public class Program {
    public static void main (String[] arg)
    {
        var config = createNewConfiguration();
        new Lwjgl3Application(new SimpleGame(), config);
    }
    static Lwjgl3ApplicationConfiguration createNewConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Epic Game");
        config.setWindowedMode(596, 596);
        config.useVsync(true);
        config.setForegroundFPS(60);
        return config;
    }
}
