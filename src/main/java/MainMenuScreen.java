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
    Sprite cube, tile, light_tile;

    private static float TILE_WIDTH = 128f;
    private static float TILE_HEIGHT = 74f;


    public MainMenuScreen()
    {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 600, 600);
        camera.update();
        cube = new Sprite(new Texture(Gdx.files.internal("cube.png")));
        tile = new Sprite(new Texture(Gdx.files.internal("tile.png")));
        light_tile = new Sprite(new Texture(Gdx.files.internal("light_tile.png")));

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
      
        for (int i = 0; i < 3; i++)
          for (int j = 0; j < 3; j++)
          {
            float x = -(j - i) * TILE_WIDTH / 2 + 256;
            float y = -(j + i) * TILE_HEIGHT / 2 + 256;
            if ((i + j) % 2 == 0)
              batch.draw(tile, x, y);
            else
              batch.draw(light_tile, x, y);
          }


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
