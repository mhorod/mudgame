import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import gdx_io.GdxGameUI;
import io.menu.views.MainMenu;
import io.views.CompositeView;

public class Program {
    public static void main(String[] arg) {
        var config = createNewConfiguration();
        new Lwjgl3Application(new GdxGameUI(new CompositeView(new MainMenu())), config);
    }

    static Lwjgl3ApplicationConfiguration createNewConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Epic Game");
        config.setWindowedMode(596, 596);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 2);
        return config;
    }
}
