package zhangman523.github.snakeio;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import zhangman523.github.snakeio.screens.DirectedGame;
import zhangman523.github.snakeio.screens.GameScreen;
import zhangman523.github.snakeio.screens.MenuScreen;
import zhangman523.github.snakeio.util.AudioManager;
import zhangman523.github.snakeio.util.GamePreferences;

public class SnakeIo extends DirectedGame {

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Assets.instance.init(new AssetManager());
        setScreen(new MenuScreen(this));
    }

}
