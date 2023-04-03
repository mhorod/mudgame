import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen
{
    private OrthographicCamera camera;
    SpriteBatch batch;
    Sprite cube;

    public MainMenuScreen()
    {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 600, 600);
        camera.update();
        cube = new Sprite(new Texture(Gdx.files.internal("cube.png")));

        batch = new SpriteBatch();
    }

    @Override
    public void show()
    {

    }

    @Override
    public void render(float delta)
    {
        ScreenUtils.clear(Color.TAN);
        batch.setProjectionMatrix(camera.combined);
        camera.update();
        batch.begin();
        batch.draw(cube, 0, 0, Gdx.graphics.getWidth(),
                   Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void resize(int width, int height)
    {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {

    }
}
