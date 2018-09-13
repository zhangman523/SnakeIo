package zhangman523.github.snakeio;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class WorldRenderer implements Disposable {

    private WorldController worldController;
    private SpriteBatch spriteBatch;


    public WorldRenderer(WorldController worldController) {
        this.worldController = worldController;
        init();
    }

    private void init() {
        spriteBatch = new SpriteBatch();
    }

    public void render() {
        spriteBatch.begin();
        worldController.snake.render(spriteBatch);
        spriteBatch.end();
    }


    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
