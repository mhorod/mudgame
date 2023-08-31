import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import gdx_io.GdxGameUI;
import io.game.GameView;
import io.views.CompositeView;

public class Program {
    public static void main(String[] arg) {
        var config = createNewConfiguration();
        new Lwjgl3Application(new GdxGameUI(new CompositeView(new GameView())), config);
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
