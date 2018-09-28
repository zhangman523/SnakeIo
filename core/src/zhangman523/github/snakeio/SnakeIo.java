package zhangman523.github.snakeio;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import zhangman523.github.snakeio.screens.DirectedGame;
import zhangman523.github.snakeio.screens.MenuScreen;

public class SnakeIo extends DirectedGame {

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Assets.instance.init(new AssetManager());
        setScreen(new MenuScreen(this));
    }

}
