package zhangman523.github.snakeio.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import zhangman523.github.snakeio.SnakeIo;

public class DesktopLauncher {
    private static boolean rebuildAtlas = false;
    private static boolean drawDebugOutline = false;

    public static void main(String[] arg) {
        if (rebuildAtlas) {
            TexturePacker.Settings settings = new TexturePacker.Settings();
            settings.maxHeight = 1024;
            settings.maxWidth = 1024;
            settings.duplicatePadding = false;
            settings.debug = drawDebugOutline;
            TexturePacker.process(settings, "../../desktop/images",
                    "../../android/assets/images", "snake");
        }
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 480;
        config.samples = 3;
        new LwjglApplication(new SnakeIo(), config);
    }
}
